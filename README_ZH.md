# Purah-zh

应该是到目前为止最简单好用的java参数校验框架 ,**基本实现用注释级别的复杂度实现规则控制,**  把除了不能省略的校验逻辑之外的部分尽可能省略

使用方式与Spring Validation类似,可以通过在method的 param 上加@CheckIt注解来使用

但是相对于Spring Validation用group控制逻辑的方式,purah对逻辑的使用会简单很多

**简单是结果,不是选项**

应该有一些待发现的bug,还有一些缺少的单元测试和文档,会慢慢修复和补充

### 0 使用方法

maven 依赖

```xml
 <dependency>
      <groupId>io.github.vajval.purah</groupId>
      <artifactId>purah</artifactId>
      <version>1.0.13</version>
 </dependency>
```

在启动类上增加注解

```java
@SpringBootApplication 
@EnablePurah(checkItAspect = true)//加上这个注解,checkItAspect 默认为true,设置为false可以关闭切面校验
public class ExampleApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

‍

### 1 基础使用

例如要对输入的姓名进行 中文姓名检测,先定义必要的逻辑

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

然后就可以像Spring Validation一样使用了,增加了@FillToMethodResult 注解可将检测结果填充到返回值中

‍

* 不添加 `@FillToMethodResult`​注解,失败会抛出 `MethodArgCheckException`​ 异常
* 添加 `@FillToMethodResult`​注解,会将检测的结果保存到返回值中.返回类型需要满足 `boolean`​ 或者 `CheckResult`​ 或者 `MethodHandlerCheckResult`​

```java
    public void test(@CheckIt("中文名字检测") String name) {  //失败抛出MethodArgCheckException

    }
    @FillToMethodResult
    public boolean testB(@CheckIt("中文名字检测")String name) {   //将CheckResult的isSuccess()boolean结果填充到返回值
        return false;
    }
    @FillToMethodResult
    public CheckResult testC(@CheckIt("中文名字检测")String name) {   //将CheckResult 填充到返回值
        return null;
    }
```

同样支持不使用切面直接获取返回结果

```java
    @Autowired
    Purahs purahs;
    public void test(String name) {  
       CheckResult checkResult = purahs.checkerOf("中文名字检测").check(name);// 不借助切面手动检测
    }
```

`@ToChecker`自带一个自动null校验

```java
public @interface ToChecker {

    String value();

    AutoNull autoNull() default AutoNull.notEnable;

}
```
可以实现在入参为null时自动成功或失败,默认notEnable 即不对null进行自动处理

```java
    @ToChecker(value = "auto_null_success", autoNull = AutoNull.success)// name为null,自动返回成功
    public boolean auto_null_success(String name) {
        return false;
    }

    @ToChecker(value = "auto_null_failed", autoNull = AutoNull.failed)// name为null,自动返回失败
    public boolean auto_null_failed(String name) {
        return true;
    }
```
### 2 多字段联合校验

如果想对拥有 多个字段的进检测行

例如 检测手机号是否与地址相符合

```java
    class People{
       String phone;
       String address;
       String id;
       People parent;
    }
    class User{
       @TestAnn("123")
       String phone;
       String address;
       String name;
    }
  

    @PurahMethodsRegBean
    public class CheckBean {

        @ToChecker("手机号所属地址检测")
        public boolean phoneAddress(
                @FVal("phone")String phone,//phone value
                @FVal("phone")TestAnn TestAnn, //phone 字段上的注解,People为null,User为@TestAnn("123")
                @FVal("address")String address //address value
        ){
            //定义逻辑,想要获取值的字段应当有getter函数
           //当user或者people对象为null时,所有字段被填充为null,但是注解的获取不受影响
            return true;
        }
    }
```

###### 使用

```java
  public void test(@CheckIt("手机号所属地址检测") People people) {   

  }
  public void test(@CheckIt("手机号所属地址检测") User user) {   

  }
  @FillToMethodResult
  public boolean test(@CheckIt("手机号所属地址检测")User user) {  
       return null;
  }

  @Autowired
  Purahs purahs;
  public void test(User user) {   
       CheckResult checkResult = purahs.checkerOf("手机号所属地址检测").check(user);   
  }
```

​`@FVal`​中的字段必须要有getter函数,没有会被填充为null,如果入参对于一个字段有Field没getter的话,会在获取value时打印`warn`​日志请注意

例如

```accesslog
2024-07-31 21:30:03.882 [main] WARN  i.g.v.p.c.resolver.ClassReflectCache - set null value because not getter function for class class io.github.vajval.purah.util.TestUser, field: id
```

### 3 多规则组合校验

purah支持对将**任意多**种规则组合为新规则,也支持对特定符合要求的Field的值进行指定规则的校验

例如 在用户注册时, 同时对user的name进行 `中文名字检测`​ ,对user 进行 `手机号所属地址检测`​

并且将其命名为`用户注册检查`​

‍

有3种方法

##### 第一种

###### 定义

```java
    @PurahMethodsRegBean
    public class checkBean {
        @Autowired
        Purahs purahs;
        @ToChecker("用户注册检查")
        public Checker<?,?> phoneAddress(){    //用combo打组合拳
            return purahs.combo("手机号所属地址检测")  //对user 进行`手机号所属地址检测`
                      .match(new GeneralFieldMatcher("name"),"中文名字检测") //对名字为name字段进行匹配,并且进行 "中文名字检测"
                      .mainMode(ExecMode.Main.all_success);//ExecMode.Main 下面有解释
        }
        //GeneralFieldMatcher 支持以 a.b.c 的多级匹配
        //也支持 "childList#10.b.name" 这样的的带list参数的多级匹配
        //也支持  "*"   "child.*"    "*.name"   "*.na?e" 这样的通配符匹配
        //也支持 "a.b.c|childList#10.b.name|*|*.name|*.na?e" 这样同时匹配多个字符串
        //除了 GeneralFieldMatcher 还支持很多,很下面有介绍,也可以自定义
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
    // 对于被ignore的check,不被视为成功也不被视为失败,被视为没有检查,不参与组合判断
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

**本项目支持自定义语法,可以以不比注释复杂的方式指定逻辑**

purah自带了一个示例用的 `example:`​语法

下面能直接实现上面的效果

```java

    public void userReg(@CheckIt("example:1[手机号所属地址检测][name:中文名字检测]") User user) {
    }
    //example语法的实现详情见`ExampleCustomSyntaxCheckerFactory`
    // 1是ExecMode.Main 中的 all_success_but_must_check_all
    //第一个中括号里的是要对对象本身进行的检查
    //第二个中括号里的是要对对象的指定字段进行检查,默认支持 abc*,abc?的简单通配符

```

实现自定义语法并不复杂,这是  `example:`​的实现,  若想实际使用需要增加判断语法是否合规的逻辑

```java
    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.startsWith("example:");
    }
    @Override
    public Checker<Object, List<CheckResult<?>>> doCreateChecker(String needMatchCheckerName) {
        // example: 0[x,y][a:b,c;e:d,e]
        String checkerExp = needMatchCheckerName.substring("example:".length()).trim();
        ExecMode.Main mainExecType = ExecMode.Main.valueOf(Integer.parseInt(String.valueOf(checkerExp.charAt(0))));
        // 0[x,y][a:b,c;e:d,e]
        // x,y
        String useCheckersExp = checkerExp.substring(checkerExp.indexOf("[") + 1, checkerExp.indexOf("]"));
        String[] rootCheckerNames = checkerSplitter.splitToList(useCheckersExp).stream().filter(StringUtils::hasText).toArray(String[]::new);
        ComboBuilderChecker checker = purahs().combo(rootCheckerNames)
                .resultLevel(ResultLevel.only_failed_only_base_logic)
                .mainMode(mainExecType);
        // a:b,c;e:d,e
        String fieldCheckerExp = checkerExp.substring(checkerExp.lastIndexOf("[") + 1, checkerExp.lastIndexOf("]"));
        if (StringUtils.hasText(fieldCheckerExp)) {
            List<List<String>> list = fieldSplitter.splitToList(fieldCheckerExp).stream().
                    map(iSplitter::splitToList)
                    .collect(Collectors.toList());
            for (List<String> strList : list) {
                String fieldStr = strList.get(0);
                String[] checkerNames = checkerSplitter.splitToList(strList.get(1)).stream().filter(StringUtils::hasText).toArray(String[]::new);
                checker.match(new GeneralFieldMatcher(fieldStr), checkerNames);
            }
        }
        return checker;
    }
```

###### 第三种 写在 application.yml里

将这个bean 注册到 springboot

```java
@ConfigurationProperties(value = "随便写的123")
@Configuration
public class PurahConfigPropertiesBean extends PurahConfigProperties {

}
```

```yml
随便写的123:
  combo_checker:
    - name: user_reg
      checkers: abc,b,c #对入参进行的检测多个用都好隔开
      mapping:   #对 入参符合要求的字段进行检测
        general: #field_matcher的类型名字 除了general外还有很多,有特殊字符要用中括号括起来
          "[address|parent_address]": 国籍检测
          "[*name*]": 姓名检测
          "age": 年龄检测
        class_name:
          "java.lang.String": sensitive_word_check
    - name: user_reg_and_phone_and_child
      checkers: user_reg
      mapping:
        wild_card:
          "[phone]": phone_check
          "[child]": user_reg
```

###### 使用

```java
   public void reg(@CheckIt("user_reg_and_phone_and_child") User user) {
   }
```

purah 有一个回调函数,会在容器刷新时(即收到`ContextRefreshedEvent`​事件时调用),实现此接口并将其注册到spring中以实现热更新

也可以直接用脚本语言搞,怎么搞就随意了

```java
public interface PurahRefreshCallBack {
    void exec(Purahs purahs);
}

```

如果你检测函数是这么写的,而且`ResultLevel`​设置为all

```java
    @PurahMethodsRegBean
    public class checkBean {
        @Autowired
        Purahs purahs;
        @ToChecker("手机号所属地址检测")
        public CheckResult<?> phoneAddress(
            @FVal("phone") String phone,
            @FVal("phone") TestAnn TestAnn, 
            @FVal("address") String address 
        ) {   
               return LogicCheckResult.success(null,"手机号所属地址非常正确");
        }
        @ToChecker("中文名字检测")
        public CheckResult<?> nameCheck(String name) {
        //......
               return LogicCheckResult.success(null,"中文名字非常正确");
        }
   }
```

对于组合规则的返回结果,格式如下

```java
MultiCheckResult{base={"execInfo":"SUCCESS","log":"SUCCESS ([]: null)"}, valueList=[{"execInfo":"SUCCESS","log":"手机号所属地址非常正确"}, {"execInfo":"SUCCESS","log":"中文名字非常正确"}]}
```

可以套好几层

如果对  id|name都进行 check1和check2 ,会返回这样的结果

```json
{   //MultiCheckResult
  "main": "success: 'id|name':'check1,check2'",
  "valueList": [
    {  //MultiCheckResult
      "main": "success: 'id':''check1,check2'",
      "valueList": [{"logic": "success:'id':check1"},{"logic": "success:'id':check2"}]//LogicCheckResult
    },
    {
      "main": "success: 'name':''check1,check2'",
      "valueList": [{"logic": "success:'name':check1"}, {"logic": "success:'name':check2"}]
    }
  ]
}
```

对于`@ToChecker` 其实还有 ignore选项,被ignore的checkResult **不**被视为成功也**不**被视为失

被视作**不存在,** ,相当于没有检查,不会对最终的组合结果产生影响

```java
    @ToChecker(value = "auto_null_ignore", autoNull = AutoNull.ignore)
    public boolean auto_null_ignore(String name) {
        return false;
    }
    @ToChecker(value = "auto_null_success", autoNull = AutoNull.success)//  null 返回true
    public boolean auto_null_success(String name) {
        return false;
    }

    @ToChecker(value = "auto_null_failed", autoNull = AutoNull.failed) //null false
    public boolean auto_null_failed(String name) {
        return true;
    }
    @ToChecker(value = "auto_null_ignore_combo")//
    public Checker<?, ?> auto_null_ignore_combo() {
        return purahs.combo("auto_null_success", "auto_null_ignore").mainMode(ExecMode.Main.all_success);
    }
    @ToChecker(value = "auto_null_ignore_combo_failed")
    public Checker<?, ?> auto_null_ignore_combo_failed() {
        return purahs.combo("auto_null_failed", "auto_null_ignore").mainMode(ExecMode.Main.at_least_one);
    }

    Assertions.assertFalse(purahs.checkerOf("auto_null_ignore_combo_failed").check(null));//结果为false
    Assertions.assertTrue(purahs.checkerOf("auto_null_ignore_combo").check(null));//结果为true
```
### 4 类型自动匹配

我们需要在用户注册时检查是否年满18岁,其中入参可能为`User`​ 或者`age`​本身

```java
class User{
    String phone;
    String address;
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
    //执行ageCheckByUser
    public void userReg(@CheckIt("年龄合法检查")User user)
    // 对user的age字段执行ageCheckByInt
    public void userReg(@CheckIt("example:1[][age:年龄合法检查]")User user)
    //执行ageCheckByInt
    public void userReg(@CheckIt("年龄合法检查")int age){ 

```

对于多字段联合校验的情况

```java
    class User{
        String phone;
        String id;
    }
    class People{
        String address;
        String name;
    }
    
    @ToChecker("test")
    public boolean testUser(
            @FVal("phone")String phone,
            @FVal("id")String id
    ){
        //......
        return true;
    }
    @ToChecker("test")
    public boolean testPeople(
            @FVal("address")String address,
            @FVal("name")String name 
    ){
        //......
        return true;
    }

```

这个时候调用是无法区分的,因为是默认支持所有的类型的 ,即`Object`​

```java
public void testCheck(@CheckIt("test")User user);//testUser or testPeople
public void testCheck(@CheckIt("test")People people)//testUser or testPeople
```

想要区分的话,需要在函数上显式增加填充root对象的参数

```java
     @ToChecker("test")
     public boolean testUser(

             @FVal(FieldMatcher.rootField) User user //加上这个参数可以在填充根对象值的同时限定类型 ,不加默认支持所有类型,即Object
             @FVal("phone")String phone,
             @FVal("id")String id
     ){
        //......
        return true;
     }
     @ToChecker("test")
     public boolean testPeople(
         @FVal(FieldMatcher.rootField) People people
         @FVal("address")String address,
         @FVal("name")String name 
     ){
        //......
        return true;
     }
     public void testCheck(@CheckIt("test")User user);//执行  testUser
     public void testCheck(@CheckIt("test")People people)//执行   testPeople
```

### 5 purah 基本规则及jsr303


1. 如果你想使用purah又担心切面与现有代码冲突,`@EnablePurah( checkItAspect = false)`,可以将切面关闭.除此之外就只有` io.github.vajval.purah.spring.ioc.RegOnContextRefresh` 会在 `ContextRefreshedEvent`事件时重新装载`PurahContext bean`.除非你在**刷新容器**时手动向`PurahContext `中注册了**不合规的checker或者FieldMatcher** 导致异常,否则在切面关闭的情况下应该不会对项目造成影响
2. 我们需要在用户注册时时进行 `用户注册检查` 和对age进行`年龄合法检查`

   直接编写即可

    ```java
    public void userReg(@CheckIt("example:1[用户注册检查][age:年龄合法检查]")User user){
    }
    ```

   如果我们想在每个用到user的函数都校验,我们可以在每个函数的入参上都加注解, 但是会变得麻烦

   **所以可以直接加到类上**

    ```java

    @CheckIt("example:1[用户注册检查][age:年龄合法检查]")
    class User {
      //....
    }
    //加到类上之后以下两种写法效果一样
    public void userReg(@CheckIt User user){
    public void userReg(@CheckIt("example:1[用户注册检查][age:年龄合法检查]")User user)
    //请注意 只有@CheckIt 中没写值的时候类上的才生效,这个只有 `用户注册检查` 会生效
    public void userReg(@CheckIt("用户注册检查")User user)
    ```
3. 关于**jsr303**

   如果你想在purah中使用jsr303检测

   注意 **purah没有任何关于jsr303的依赖,使用者需要自行选择期望的依赖的版本,并编写类似下方逻辑的代码**

    ```java
    @Name("jsr303")
    @Component
    public class JSR303Checker<INPUT_ARG, RESULT> implements Checker<INPUT_ARG, RESULT> {
        @Override
        public CheckResult<RESULT> check(InputToCheckerArg<INPUT_ARG> inputToCheckerArg) {
            Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false)
                    .buildValidatorFactory().getValidator();
            if(inputToCheckerArg.isNull()){
                //.......
            }
            INPUT_ARG inputArg = inputToCheckerArg.argValue();
            Set<ConstraintViolation<INPUT_ARG>> constraintViolations = validator.validate(inputArg);
            //自行转换为 CheckResult
        }
    }
    ```

   就可以用了

    ```java
    public void userReg(@CheckIt("jsr303")User user)
    ```
4. 也许你期望@CheckIt 能够像jsr303一样对所有Field 都嵌套处理,但是默认不支持(颗粒度太大),可以通过**第8点嵌套结构多级Field Checkit检测**开启(往下看)

    ```java

    class User {
        @CheckIt("id检测")    //不生效
        Long id;
        @CheckIt("姓名检测")   //不生效
        String name;
        @CheckIt("${id}")   //不生效
        String address;
   }
    ```

### 6 自定义注解检测

如果希望在字段上自定义注解,然后在收到对象时对所有的字段进行自定义注解检测

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
如果你想通过继承默认的`CustomAnnChecker`来实现自定义注解检测

可以这么写,**不受限**于下面的方法

1. 编写能处理注解和值的函数
2. 写一个checker类继承自CustomAnnChecker,给这个类上加`@Name("自定义注解检测")`​ 注解
3. 按照指定格式将函数放到编写的checker类里,有多少写多少

**指定格式**

第一个参数为自定义的注解,第二个为要检查的参数,参数可以被InputToCheckerArg包裹,InputToCheckerArg包含注解及Field信息

格式规定来源于继承的`CustomAnnChecker`​的规定,如果觉得不够好,**也可以新写一个**.点开`CustomAnnChecker`​就会发现并不需要多少逻辑

CustomAnnChecker 用**反射**实现的的所以增加逻辑会很便捷,但是**绝对没有if快**
```java
@Name("自定义注解检测")
@Component
public class MyCustomAnnChecker extends CustomAnnChecker {
    public MyCustomAnnChecker() {
        super(ExecMode.Main.all_success, ResultLevel.only_failed_only_base_logic);
    }
    //下面3个检测函数,都会自动匹配并生效
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

‍

###### 使用

需求的例子用自定义语法就可以简单实现,`*`​是通配符,所有字段的意思

‍

```java
    //如果对id字段进行自定义注解检测,如aId  ,bId , 只用把`*` 改成 `*Id` 即可
    public void userReg(@CheckIt("example:1[][*:自定义注解检测]")User user){

    }
    //或者
    public void reg(User user) {
        MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
        .match(new GeneralFieldMatcher("*"), "自定义注解检测")
        .check(user);
    }

```

也许会有同一个注解放到了不同的class但是名字相同的字段上

```java
    @CNPhoneNum(errorMsg = "phone num wrong")
    public String phone
    @CNPhoneNum(errorMsg = "phone num wrong")
    public Long phone
```

那么得编写下面两个函数,会进行类型自动匹配,只用写上去就行了,别的不用管

```java
    public boolean cnPhoneNum(CNPhoneNum cnPhoneNum, Long value) {
        return cnPhoneNum(cnPhoneNum,value.toString()).isSuccess();
    }
    public CheckResult<?> cnPhoneNum(CNPhoneNum cnPhoneNum, String strValue) {
        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
           return LogicCheckResult.successBuildLog(str, "正确的");
        }
        return LogicCheckResult.failed(str.argValue(), str.fieldPath() + ":" + cnPhoneNum.errorMsg());
    }


```

### 7 复杂嵌套结构自定义注解检测

‍

项目通常有要求,对于嵌套结构的对象,要对**嵌套的所有有注解的字段**进行搜集并且check检测

虽然嵌套可以有非常多层,但是往往只对**项目开发者自己编写的类**进行嵌套寻找有注解的字段

输入一个user 我们显然只想对Field people进行嵌套检查,Long String 这种不是我们定义的class多半是不需要进去找字段的

```java
package org.MyCompany;

class People {
    @CNPhoneNum(errorMsg = "phone num wrong")
    String phone;
    @NotEmptyTest(errorMsg = "this field cannot empty")
    String name;
}
package org.MyCompany2;

class User {
    @Range(min = 1, max = 10, errorMsg = "range wrong")
    Long id;

    People people;//这是本公司的class 所以里面的被注解的字段也要检查


}
```

purah 自带了 一个AnnByPackageMatcher,允许输入要嵌套查询的 Class的package,

不符合要求的,如 `java.lang.*`​ 等,不会嵌套查询

实现 `needBeCollected(Field field)`​ 函数来确定那哪些字段的值,需要被搜集起来check

```java
  public void reg(User user) {
        //对package 符合org.MyCompany.*和org.MyCompany2.*的Field进行嵌套检查
        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("org.MyCompany.*|org.MyCompany2.*") {
        @Override
        protected boolean needBeCollected(Field field) {
             //如果Field上有需要检测的注解,就把值搜集起来
             Set<Class<? extends Annotation>> annotationSet= Sets.newHashSet(Range.class,CNPhoneNum.class,NotEmptyTest.class);
             for (Class<? extends Annotation> aClass : annotationSet) {
                 if( field.getDeclaredAnnotation(aClass)!=null){
                      return true;
                 }
             }
              return false;
        }
  }      
   //会搜集字段 id people.phone  people.name 进行检测,注意people字段没注解所以不会被搜集
   //执行
   MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
        .match(annByPackageMatcher, "自定义注解检测")
        .check(user);
}
```

如果层级只有两层的话,就简单了,直接写 用GeneralFieldMatcher  `*|*.*`​ 就行,三层就是`*|*.*|*.*.*`​ ,知道匹配结果的话也可以直接写死

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
    public void userReg(@CheckIt User user){
    }
```

如果觉得 FieldMatcher 的逻辑繁琐会影响性能,可以通过启用缓存来实现

**缓存加速后FieldMatcher中的逻辑只用执行一次,**

但是只能在确定获取到的**字段不会随着入参变化而变化时**才可以使用缓存加速,

支持多级的FieldMatcher 通常 4 种

1. ​`FixedMatcher("people.name|people.phone|noExist")`​ 返回 map 的  `noExist`​ 的值为null,会被checker检查,支持缓存
2. ​`NormalMatcher("people.name|people.phone|noExist")`​ 返回 map 没有  `noExist`​  字段,不会被checker检查,当people为null时,`people.phone`​字段也不存在,更不会被检查,当不包含多级获取时,支持缓存.有多级获取不支持缓存  (可以理解为将对象视为json)
3. ​`GeneralFieldMatcher("*.name|people.phone|noExist")``​ 对于写死的`noExist`​和`people.phone`​ 当作`FixedMatcher`​ 处理,对于`*.name`​ 等需要匹配的当作`NormalMatcher`​ 处理,是否缓存视情况而定
4. ​`AnnByPackageMatcher`​ 上面有例子 默认缓存

但是有3种情况即使,FieldMatcher 支持缓存也不会启动缓存

‍

1. ​`@EnablePurah(argResolverFastInvokeCache = false)`​,关闭当然不生效
2. ```java
    class People{   
          @Test("id") String id;
          @Test("child") People child;   //
          @Test("child") List<People> childList;
          @Test("child") SuperPeople  superChild ;
    }

    class final SuperPeople extend People{
          @Test("superId") String id;   // ann change
    }

    //FixedMatcher("id|child") 支持缓存
    //FixedMatcher("id|child.id")不支持缓存
    //FixedMatcher("id|superChild.id") 支持缓存
    //因为为null时无法获取class,所以无法确定class是子类中的哪一个.
    //反之如果Field的class有final,那么这个Field的class无法被继承,所以可以确定null值的class的所有字段也就可以缓存
    //todo 待优化,争取不final也支持
    ```
3. list 获取不支持缓存, 如 `FixedMatcher("childList#100.id")`​,不支持缓存,因为list长度不固定

purah扩展性很强,`FieldMatcher`​ 可以像这样随意增加,

注意这个是是顺着启动类的目录向下扫描的,不要放的层级比启动类还高

```java
@Name(NAME)
@ToBaseMatcherFactory
public class ReverseStringMatcher extends BaseStringMatcher {
    public static final String NAME = "reverse_ioc_test";
    public ReverseStringMatcher(String matchStr) {
        super(matchStr);
    }
    @Override
    public boolean match(String field, Object belongInstance) {
        String reverse = new StringBuilder(matchStr).reverse().toString();
        return Objects.equals(reverse, field);
    }
}
public class PurahUtils {
    @Autowired
    Purahs purahs;
    public static class Match {
        public static String reverse=ReverseStringMatcher.NAME;
    }
    public static void main(String[] args) {
        //两种获取方法效果一样
        FieldMatcher reverseTest = purahs.matcherOf(PurahUtils.Match.reverse).create("reverse_test");
        ReverseStringMatcher reverseStringMatcher = new ReverseStringMatcher("reverse_test");
        String reverseMatcherName = PurahUtils.Match.reverse;//这么用容易定位
    }
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

    //实现对自定义注解CheckIt 的检测
    public MultiCheckResult<CheckResult<?>> checkItAnn(CheckIt checkIt, InputToCheckerArg<Object> inputToCheckerArg) {
        Purahs purahs = purahs();
        String[] checkerNames = checkIt.value();
        ExecMode.Main checkItMainMode = checkIt.mainMode();
        ResultLevel checkItResultLevel = checkIt.resultLevel();
        ComboBuilderChecker checker = purahs.combo(checkerNames).resultLevel(checkItResultLevel).mainMode(checkItMainMode);
        return checker.check(inputToCheckerArg);
    }

    public Purahs purahs(){
        return purahs;
    }
    //往里加函数以使其他注解生效
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

###### 使用

```java
//使用
public void userReg(@CheckIt("example:1[][id|people.phone|people.name|people:自定义注解检测]")User user){
}
```

也许你想让checkIt支持 spel或者其他语法

```java

class People {
    @CheckIt("${name}")
    String phone;
    @CheckIt("${phone}")
    String name;
}

class User {
    @CheckIt("id检测")
    Long id;
    @CheckIt("人员信息检测")
    People people;
}
```

那么可以这样写一个基础类,复制完就行不用管了

```java
public abstract class AllFieldCheckItSpelChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {

    final ExecMode.Main mainExecType;
    final ResultLevel resultLevel;
    public AllFieldCheckItSpelChecker(ExecMode.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;
    }
    protected abstract Purahs purahs();
    protected abstract String spel(String value, Map<String, ?> map,InputToCheckerArg<Object> inputToCheckerArg);
    protected abstract FieldMatcher fieldMatcher(InputToCheckerArg<Object> inputToCheckerArg);
    @Override
    public MultiCheckResult<CheckResult<?>> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        Purahs purahs = purahs();
        ArgResolver argResolver = purahs.argResolver();
        FieldMatcher fieldMatcher = this.fieldMatcher(inputToCheckerArg);
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = argResolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);
        Map<String, ?> map = matchFieldObjectMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().argValue()));
        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(mainExecType, resultLevel);
        for (InputToCheckerArg<?> value : matchFieldObjectMap.values()) {
            CheckIt checkIt = value.annOnField(CheckIt.class);
            String[] array = (String[]) Arrays.stream(checkIt.value()).map(i -> spel(i, map,inputToCheckerArg)).toArray();
            ComboBuilderChecker combo = purahs.combo(array).mainMode(checkIt.mainMode()).resultLevel(checkIt.resultLevel());
            multiCheckerExecutor.add(() -> combo.check(value));
        }
        String log = inputToCheckerArg.fieldPath() + "  " + this.name();
        return multiCheckerExecutor.toMultiCheckResult(log);
    }
}
```

然后就可以随便用了

可以写一个这样的checker

```java
@Component
@Name("对所有字段CheckIt注解进行填充并且检测")
public class TestChecker  extends AllFieldCheckItSpelChecker{
    @Autowired
    Purahs purahs;
    public TestChecker() {
        super(ExecMode.Main.all_success, ResultLevel.all);
    }
    @Override
    protected Purahs purahs() {
        return purahs;
    }
    @Override
    protected String spel(String value, Map<String, ?> map,InputToCheckerArg<Object> inputToCheckerArg) {
        //想匹配咋填充就咋填充
        return null;
    }

    @Override
    protected FieldMatcher fieldMatcher(InputToCheckerArg<Object> inputToCheckerArg) {
        //想匹配啥字段就啥字段,多级的就`AnnByPackageMatcher` 
        //嫌麻烦直接`new GeneralFieldMatcher(*|*.*|*.*.*|*.*.*.*|*.*.*.*.*)`
        return null;
    }
}
```

注意,这个checker的入参是root对象本身,自定义语法应该这么用

```java
     public void userReg(@CheckIt("example:1[对所有字段CheckIt注解进行填充并且检测][id|people.phone|people.name|people:自定义注解检测]")
         User user){
     }
```

‍

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
```

DDD 优化后的写法

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
@EnablePurah(enableCache = true)//全局打开默认false
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

//todo 待完成

```java
public class ValidArg<T> {
    T value;
    boolean valid = false;
}
```

‍

### 10 checkIt 切面抛出的异常和@FillToMethodResult 填充的数据

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
    only_failed_only_base_logic(4);
}
```

@FillToMethodResult 填充的就是MethodHandlerCheckResult, boolean的话就是methodHandlerCheckResult.isSuccess();

‍
