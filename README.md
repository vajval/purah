# Purah

应该是最好的java参数校验框架

把除了不能省略的校验逻辑之外的部分全部省略

使用方式与Spring Validation类似,但是相对于Spring Validation通过类上固定注解,

再用略微复杂的group控制逻辑的方式

purah可以

再用注释级别的复杂度实现控制逻辑


使用方法,在启动类上加@EnablePurah注解

```java
@SpringBootApplication
@EnablePurah
public class ExampleApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

### 1 普通对象校验

例如要对输入的姓名进行检测,先定义必要的逻辑

```java
    @PurahMethodsRegBean //将bean中有注解的函数注册为规则
    public class checkBean {
        @ToChecker("中文名字检测")// 将函数转换为规则并且注册
        public boolean nameCheck(String name) {
            //......
            return true;
        }
    }
```

然后就可以像spring valid一样使用了,在使用@CheckIt的情况下,不成功会抛出异常,也可以使用@FillToMethodResult 直接将结果放到函数的返回结果里

使用方法与spring valid相同,失败会抛出 `MethodArgCheckException` 异常

增加了@FillToMethodResult注解后不会抛出异常,而是将检测的结果保存到返回值中

```java
    public void test(@CheckIt("中文名字检测") String name) {  //失败抛出MethodArgCheckException 
 
    }
    @FillToMethodResult
    public boolean test(@CheckIt("中文名字检测")String name) {   //将CheckResult的isSuccess()boolean结果填充到返回值
        return null;
    }
    @FillToMethodResult
    public CheckResult test(@CheckIt("中文名字检测")String name) {   //将CheckResult 填充到返回值
        return null;
    }
```

支持不使用切面直接获取返回结果

```java
    @Autowired
    Purahs purahs;
    public void test(String name) {      
       CheckResult checkResult = purahs.checkerOf("中文名字检测").check();// 不借助切面手动检测
    }
```

### 2 多字段联合校验

如果想要在User注册时,检测手机号是否符合地址.可以编写以下逻辑

###### 定义

```java
    class User{
       @TestAnn("123")
       String phone;
       String address
       String name;
    }
  

    @PurahMethodsRegBean
    public class checkBean {
        //定义逻辑,想要获取值的字段应当有getter函数
        //当user为null时,所有字段被填充为null,但是注解的获取不受影响
        @ToChecker("手机号所属地址检测")
        public boolean phoneAddress(
                @FVal("phone")String phone,//phone value
                @FVal("phone")TestAnn TestAnn, //phone 字段上的注解,没有为null
                @FVal("address")String address //address value
        ){
            //......
            return true;
        }
    }
```

###### 使用

```java
  public void test(@CheckIt("手机号所属地址检测") User user) {   

  }
  @FillToMethodResult
  public boolean test(@CheckIt("手机号所属地址检测")User user) {  
       return null;
  }

  @Autowired
  Purahs purahs;
  public void test(User user) {   
       CheckResult checkResult = purahs.checkerOf("手机号所属地址检测").check();   
  }
```



### 3 多规则组合校验

本项目支持对将任意多种规则组合,也支持对指定符合条件阿德字段

在用户注册时,同时对用户进行名字和手机号所属地址检测

有两种方法

##### 第一种

###### 定义

```java
    @PurahMethodsRegBean
    public class checkBean {
        @Autowired
        Purahs purahs;
        @ToChecker("用户注册检查")
        public Checker<?,?> phoneAddress(){
            //对名字为name字段进行匹配,并且进行 "中文名字检测"
            return purahs.combo("手机号所属地址检测")
                      .match(new GeneralFieldMatcher("name"),"中文名字检测")
                      .mainMode(ExecMode.Main.all_success);//ExecMode.Main 下面有解释
        }
    }

```

###### 使用

```java
   public void reg(@CheckIt("用户注册检查") User user) {
   }
```

对于多个规则的执行方法(ExecMode.Main),可以通过选择不同的类型来控制判断逻辑

```java
public class ExecMode {
    public enum Main {    
        // 全成功才行,有错不继续 = 快速失败
        all_success(0),//默认值
        // 全成功才行,有错也要检查完
        all_success_but_must_check_all(1),
        // 一个就行,有错不继续检查
        at_least_one(2),
        // 一个就行,有错也要检查完
        at_least_one_but_must_check_all(3);

//......
```

##### 第二种 推荐

本项目支持自定义语法,下面能直接实现上面的效果

###### 定义并且直接使用

```java
    //example语法的实现详情见`MyCustomSyntaxCheckerFactory`   
    //1是ExecMode.Main 中的 all_success_but_must_check_all
    public void userReg(@CheckIt("example:1[手机号所属地址检测][name:中文名字检测]") User user) {
     //第一个中括号里的是要对对象本身进行的检查
     //第二个中括号里的是要对对象的指定字段进行检查,默认支持 abc*,abc?的简单通配符
    }
```

###### 第三种 卸写在 application.yml里

可以实现在文档下面的callback实现热更新

```java
purah:
  combo_checker:
    - name: 用户注册检查
      mapping:
        wild_card:  # 要使用的 fieldMatcher类上的注解名字 
          "[{address,parent_address}]": national_check
          "[*name*]": name_validity_check
          "[age]": age_check
        class_name:
          "java.lang.String": sensitive_word_check
```

###### 使用

```java
   public void reg(@CheckIt("用户注册检查") User user) {
   }
```



### 4 类型自动匹配

我们需要在用户注册时检查是否年满18岁,其中入参可能为User 或者age本身

```java
    class User{
       String phone;
       String address
       String name;
       int age;
    }
 
```

###### 定义

```java
    @PurahMethodsRegBean
    public class checkBean {
        @ToChecker("年龄合法检查")
        public boolean ageCheckByUser(User user) {
            return user.age >= 18;
        }
        @ToChecker("年龄合法检查")
        public boolean ageCheckByInt(int age) {
            return age >= 18;
        }
    }
```

可以直接使用

```java
    public void userReg(@CheckIt("年龄合法检查")User user){ //执行ageCheckByUser
    }
    public void userReg(@CheckIt("example:1[][age:年龄合法检查]")User user)// 对user的age字段执行ageCheckByInt
    public void userReg(@CheckIt("年龄合法检查")int age){ //执行ageCheckByInt
    }

```



### 5 checkIt注解规则

我们需要在进行已有的用户注册检查,然后还要检查年龄.

直接编写即可

```java
public void userReg(@CheckIt("example:1[用户注册检查][age:年龄合法检查]")User user){
}
```

如果我们想在每个用到user的函数都校验,我们可以在每个函数的入参上都加注解, 但是会变得麻烦

所以可以直接加到类上

```java

@CheckIt("example:1[用户注册检查][age:年龄合法检查]")
class User {
  //....
}
//加到类上之后以下两种写法效果一样
public void userReg(@CheckIt User user){
public void userReg(@CheckIt("example:1[用户注册检查][age:年龄合法检查]")User user)
//请注意 只有@CheckIt 中没写值的时候类上的才生效,这个只有用户注册检查会生效
public void userReg(@CheckIt("用户注册检查")User user)
```

也许你想对所有字段都这样,但是默认不支持,可以通过第七点开启(往下看)

```java
//不生效
class User {
    @CheckIt("id检测")
    Long id;
    @CheckIt("姓名检测")
    String name;

```


### 6 自定义注解检测

如果希望在字段上自定义注解,然后在收到对象时对所有的字段进行自定义注解检测

可以编写如下代码

注意,这些注解都是在单元测试里自己定义的,并非受限于jsr303.完全可以随意编写

```java
class User {
    @Range(min = 1, max = 10, errorMsg = "range wrong")
    public Long id;
    @NotEmptyTest(errorMsg = "this field cannot empty")
    public String name;
    @CNPhoneNum(errorMsg = "phone num wrong")
    public String phone;
    @NotNull(errorMsg = "norBull")
    public Integer age;
}
```

想对所有字段进行检测的步骤

1 编写不可省略的逻辑部分

2 将逻辑放到按照指定格式编写的method.其中method应该写到继承了CustomAnnChecker 的类中, 给这个类起名字 "自定义注解检测"

3 对每个字段 进行 "自定义注解检测"

**指定格式  第一个参数为自定义的注解,第二个为要检查的参数,参数可以被InputToCheckerArg包裹,InputToCheckerArg包含注解及Field信息**

```java
@Name("自定义注解检测")
@Component
public class MyCustomAnnChecker extends CustomAnnChecker {
    public MyCustomAnnChecker() {
        super(ExecMode.Main.all_success, ResultLevel.only_failed_only_base_logic);
    }
    //下面3个检测函数
    public CheckResult<?> cnPhoneNum(CNPhoneNum cnPhoneNum, InputToCheckerArg<String> str) {
        String strValue = str.argValue();
        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return LogicCheckResult.successBuildLog(str, "正确的");
        }
        return LogicCheckResult.failed(str.argValue(), str.fieldPath() + ":" + cnPhoneNum.errorMsg());
    }

    public boolean notNull(NotNull notNull, Integer age) {
        return age != null;
    }

    public CheckResult<?> range(Range range, InputToCheckerArg<Number> num) {
        Number numValue = num.argValue();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return LogicCheckResult.failed(num.argValue(), (num.fieldPath() + ":" + range.errorMsg()));
        }
        return LogicCheckResult.successBuildLog(num, "参数合规");
    }
}
```

使用方法,实例用的自定义语法就可以简单实现,`*`是通配符,所有字段的意思

如果只检查所有的id字段,如aId  ,bId , 只用把`*` 改成 `*Id` 即可

```java
    public void userReg(@CheckIt("example:1[][*:自定义注解检测]")User user){  

    }
    //或者
    public void reg(User user) {
        MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
                .match(new GeneralFieldMatcher("*"), "自定义注解检测")
                .check(user);
    }

```

```java
    @CNPhoneNum(errorMsg = "phone num wrong")
    public String phone
    @CNPhoneNum(errorMsg = "phone num wrong")
    public Long phone
```

也许会有一个注解放到了不同class的字段上,那么得编写两个函数,

会进行类型自动匹配,只用写上去就行了,别的不用管

```java
    public boolean cnPhoneNum(CNPhoneNum cnPhoneNum, Long value) {
        return cnPhoneNum(cnPhoneNum,value.toString());
    } 
    public boolean cnPhoneNum(CNPhoneNum cnPhoneNum, String strValue) {
        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return LogicCheckResult.successBuildLog(str, "正确的");
        }
        return LogicCheckResult.failed(str.argValue(), str.fieldPath() + ":" + cnPhoneNum.errorMsg());
    }

 
```



### 7 复杂嵌套结构自定义注解检测

项目通常有要求,对于嵌套结构的对象,要对嵌套的所有字段进行搜集并且检查

虽然嵌套可以有非常多曾,但是往往只对项目本身定义的Class进行检查

输入一个user 我们显然只想对Field people进行嵌套检查,Long String 这种不是我们定义的class多半是不需要进去找字段的

```java
package org.Company;

class People {
    @CNPhoneNum(errorMsg = "phone num wrong")
    String phone;
    @NotEmptyTest(errorMsg = "this field cannot empty")
    String name;
}
package org.Company;

class User {
    @Range(min = 1, max = 10, errorMsg = "range wrong")
    Long id;

    People people;//这是本公司的class 所以里面的被注解的字段也要检查


}
```

purah 自带了 一个AnnByPackageMatcher,允许输入要嵌套查询的 Class的package,

不符合要求的,如 `java.lang.*` 等,不会嵌套查询

实现 `needBeCollected(Field field)` 函数来确定那哪些字段的值,需要被搜集起来检查

```java
 public void reg(User user) {
         //对package 符合org.MyCompany.*和org.MyCompany2.*的Field进行嵌套检查
        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("org.MyCompany.*|org.MyCompany2.*") {
            @Override
            protected boolean needBeCollected(Field field) {
               //如果Field上有需要检测的注解,就把值搜集起来
                Set<Class<? extends Annotation>> annotationSet= Sets.newHashSet(Range.class,CNPhoneNum.class,NotEmptyTest.class);
                for (Class<? extends Annotation> aClass : annotationSet) {
                    if(field.getDeclaredAnnotationsByType(aClass)!=null){
                        return true;
                    }
                }
                return false;
            }
        };
        //会搜集字段 id people.phone  people.name 进行检测,注意people字段没注解所以不会被搜集
        //执行
        MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
                .match(annByPackageMatcher, "自定义注解检测")
                .check(user);
  }
```

如果层级只有两层的话,就简单了,直接写  `*|*.*` 就行,三层就是`*|*.*|*.*.*` ,知道的话也可以直接写死

```java
    //以下4种写法和上面的效果一样
     public void userReg(
                @CheckIt("example:1[][*|*.*:自定义注解检测]")User user){
      }
     public void userReg(
                @CheckIt("example:1[][id|people.phone|people.name:自定义注解检测]")User user){
     }
  
     public void reg(User user) {
            MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
                    .match(new GeneralFieldMatcher("*|*.*"), "自定义注解检测")
                    .check(user);
     }



    @CheckIt("example:1[][id|people.phone|people.name:自定义注解检测]")
    class User {
     //....
    }
    public void userReg( @CheckIt User user){
     }
```

项目扩展性很强,不一定非要用自带的FieldMatcher,完全可以自己实现很多

如果觉得 FieldMatcher 的逻辑繁琐会影响性能,可以通过来实现缓存

在 argResolverFastInvokeCache = true, 且编写的FieldMatcher supportCache() 为true 的情况下

FieldMatcher中的逻辑只用执行一次,

```java
@EnablePurah(argResolverFastInvokeCache = true)
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}

```


对于某些情况是无法嵌套检测到

```java
class User {
    @Range(min = 1, max = 10, errorMsg = "range wrong")
    Long id;

    People people;//这是本公司的class 所以里面的注解也要检查

  
    List<People> peopleList;//可以嵌套检测
    List<List<People>> peopleListList;// 受限于java的泛型设计,无法嵌套检测
    List List;//无法嵌套检测
}
```

### 8 嵌套结构 多级FieldCheckit检测

项目中可能会希望进行如下检测

```java

class People {
    @CheckIt("电话检测")
    String phone;
    @CheckIt("姓名检测")
    String name;
}

class User {
    @CheckIt("id检测")
    Long id;
    @CheckIt("人员信息检测")
    People people;
}
```

如此编写是个好主意,但是如果默认支持的话颗粒度实在太大,请在  "自定义注解检测" 类中手动编写

CustomAnnChecker 里有写好的部分已经被注释掉了,粘贴到MyCustomAnnChecker 中就行

###### 定义

```java
//定义
@Name("自定义注解检测")
@Component
public class MyCustomAnnChecker extends CustomAnnChecker {
    @Autowired
    Purahs purahs;

    public Purahs purahs(){
        return purahs;
    }
    //实现对自定以注解CheckIt 的检测
    public MultiCheckResult<CheckResult<?>> checkItAnn(CheckIt checkIt, InputToCheckerArg<Object> inputToCheckerArg) {
        Purahs purahs = purahs();
        String[] checkerNames = checkIt.value();
        ExecMode.Main checkItMainMode = checkIt.mainMode();
        ResultLevel checkItResultLevel = checkIt.resultLevel();
        ComboBuilderChecker checker = purahs.combo(checkerNames).resultLevel(checkItResultLevel).mainMode(checkItMainMode);
        return checker.check(inputToCheckerArg);
   }
}


```

###### 使用

```java
//使用
public void userReg(@CheckIt("example:1[][id|people.phone|people.name|people:自定义注解检测]")User user){
}
```

### 9 上下文缓存

在项目中可能会遇到如下问题

如下所示,为了安全会被检测两次

```java
 
   public class TestController {
        @Autowired
        TestService testService;
        public void reg(User user) {
            testService.reg(user);
            //........
        }

   }

   public class TestService {
        @Autowired
        HHHHService hhhhService;
        public void reg(User user) {
            //先从db检测参数是否合规
            hhhhService.reg(user);
            //........
        }
   }

   public class HHHHService {
        public void reg(User user) {
              //为了防止没有检测,先从db检测参数是否合规
              //........
        }
   }
}
```

ddd 优化后的写法

```java
    class SafeUser {
        User user ;
    }
    public class TestController {
        @Autowired
        TestService testService;
        public void reg(User user) {
            boolean success= check(user); 
            if(success){
               SafeUser safeUser  =new SafeUser(user)
               testService.reg(safeUser);
             } 
        }
   }
   public class TestService {
        @Autowired
        HHHHService hhhhService;
        public void reg(SafeUser safeUser) {
            User user=safeUser.user;
            hhhhService.reg(user);
            //........
        }
   }
   public class HHHHService {
        public void reg(SafeUser safeUser) {
              User user=safeUser.user;
              //........
        }
   }
```

purah的写法,在开启基于threadlocal的上下文缓存的情况下,用户检测只会被调用一次

```java
@SpringBootApplication
@EnablePurah(enableCache = true)//默认全局开启
public class ExampleApplication {
    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }
}
//或者
@MethodCheckConfig(enableCache = true)
public void reg(@CheckIt("用户检测")User user) {
                testService.reg(user);   
}

```

在全局开启的情况下,可以这么写,但是不推荐,@checkIt 基于切面,不是bean中调用不生效,容易写bug

```java
  
   public class TestController {
        @Autowired
        TestService testService;
        public void reg(@CheckIt("用户检测")User user) {
                testService.reg(user);   
        }

    }

    public class TestService {
        @Autowired
        HHHHService hhhhService;
        public void reg(@CheckIt("用户检测") User user) {
            hhhhService.reg(user);
        }
    }

    public class HHHHService {
        public void reg(@CheckIt("用户检测")User user) {

        }
    }
    public class ShowService {
        public void reg(@CheckIt("用户检测")User user) {
            testController.reg(user);
            testService.reg(user);
            hhhhService.reg(user);
        }
    }
    public static void main(String[] args) {
       //共检测 4次
        testController.reg(user);//只检测一次
        testService.reg(user);//只检测一次
        hhhhService.reg(user);//只检测一次
        showService.reg(user);//只检测一次
     }
```







### 配置文件方式编写及容器刷新

自定义组合规则,application.yml

```yml
purah:
  combo_checker:
    - name: user_reg
      mapping:
        wild_card:  # 要使用的 fieldMatcher类上的注解名字 
          "[{address,parent_address}]": national_check
          "[*name*]": name_validity_check
          "[age]": age_check
        class_name:
          "java.lang.String": sensitive_word_check
```


实现这个函数,可以在容器刷新时调用(`ContextRefreshedEvent` 事件),

原有规则被清空重新注册,

可以自行注册

```java
public interface PurahRefreshCallBack {
    void exec(Purahs purahs);
}
```



### checkIt 切面抛出的异常和@FillToMethodResult 填充的数据

不论是 切面抛出的exceptipn还是@FillToMethodResult 填充的数据

本质上都是对MethodHandlerCheckResult的封装


```java
public class MethodArgCheckException extends BasePurahException {

    final MethodHandlerCheckResult checkResult;

    public MethodArgCheckException(MethodHandlerCheckResult checkResult) {
        super(checkResult.execInfo().name());
        this.checkResult = checkResult;

    }
}
public void checkThreeUserThrow(@CheckIt({"test","test2"}) User user0,
                                @CheckIt("test") User user1,
                                @CheckIt("test") User user2) {
}
}

public void main() {
      MethodHandlerCheckResult 以ArgCheckResult的方式 储存了每个参数的校验结果,通过 argResultOf(int index)获取
      ArgCheckResult 储存了每个规则的结果

      methodHandlerCheckResult.argResultOf(0).resultOf("test")
      methodHandlerCheckResult.argResultOf(0).resultOf("test2")
      methodHandlerCheckResult.argResultOf(1).resultOf("test")
      //输入 ResultLevel,获取所有的失败的直接逻辑校验部分结果
      List<LogicCheckResult<?>> failedList= methodHandlerCheckResult.childList(ResultLevel.only_failed_only_base_logic);
   
}
//ResultLevel 等级
public enum ResultLevel {
    //所有的结果,不论成功与否,是不是校验逻辑直接返回的结果
    all(1),
    //所有的结果,不论成功与否,只要校验逻辑直接返回的结果
    all_only_base_logic(2),
    //只要失败的结果
    only_failed(3),
    //只要失败的结果,只要校验逻辑直接返回的结果
    only_failed_only_base_logic(4),
    //只要有异常的结果
    only_error(0);
}
```

@FillToMethodResult 填充的就是MethodHandlerCheckResult, boolean的话就是methodHandlerCheckResult.isSuccess();