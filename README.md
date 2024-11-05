### 引言

在开发程序时，开发者通常需要在函数的入口处进行数据校验，以保证代码安全性以及数据的完整性

而Spring Boot 正好自带了一个强大的 Validation，使得我们可以快速进行简单的参数校验，并且其拥有良好的扩展性

#### 那为什么不直接只用Spring Validation？ 还要再写一个新框架
`spring validation` 基于 `jsr303` 和`jsr380`的设计与实现固然很完善，也历经考验依然屹立不倒。
其控制逻辑的方式为类中字段固定注解，再用`group`进行分类，可以快速的实现功能

但是随着业务的逐渐复杂，`group`的数量和复杂的业务校验部分会越来越多，代码的复杂度往往会出现**灾难级的指数增长** ，使用最后导致代码的可读性和可维护性下降很多。

而且对于种类数量繁多且繁琐的业务校验，`spring validation` 会显得力不从心。在可读性下降到一定程度之后，维护的难度会直线上升。**极端情况下**不如把`spring validation`删了，再用if else 重写逻辑更加直观还好维护

### Purah
`purah` 是为了弥补这个缺点出现的，**基本到达简洁明了的极限，用起来不会比用自然语言写注释复杂**

可以帮助开发者以**不超过注释的复杂度**来完成对业务校验的控制，并且**不会**对原有代码和逻辑造成影响，相对于`spring validation`，`purah`处理复杂的业务校验更加的简单，可以通过定义规则并将**复数规则自由嵌套组合**来实现对复杂业务的友好支持

而且可以**和`spring validation`配合使用，** 使得代码能快速**同时完成简单的参数校验和复杂的业务校验**


### 入门例子

在启用依赖后只要编写下面的代码来完成参数校验

**只花费写注释的精力，完成了对参数校验逻辑的控制，同时本身也起到了注释的作用**


```java
@Service
@PurahMethodsRegBean//将类中有`@ToChecker`注解的函数注册为规则
public class UserService {

    //将规则自由组合并且使用，`example:` 是指用自带的示例语法的意思，支持语法扩展
    
    public void userReg(@CheckIt("example:0[用户信息检测][phone:手机号码检测]") User user) {
  
        //对user本身 进行`用户信息检测`对 phone字段进行 `手机号码检测`
        //失败会抛出MethodArgCheckException，可进行全局异常拦截
        //不想进行全局异常拦截可以用手动挡方法，下面有介绍
    }

    //不影响函数正常使用，将这个函数注册为规则，并且设置入参为null自动失败
    
    @ToChecker(value = "用户信息检测"， autoNull = AutoNull.failed)
    public boolean userCheck(User user) { 
        //...
        return true;
    }

    //自定义一个简单的失败信息
    
    @ToChecker(value = "手机号码检测"， failedInfo = "手机号码错误: ${arg}")
    public boolean phoneCheck(String phone) {
        //想返回复杂结果需要将函数的返回类型由boolean改为CheckResult，可以自由填充，推荐用LogicCheckResult.success("123");
        return phone.length() == 11;
    }
}
```

支持和`spring validation` 一样进行统一错误处理，也支持不用切面手动调用

```java
@Controller
@RequestMapping(value = "test/zero")
public class TestController {
    @Autowired
    UserService userService;
    @GetMapping(value = "/reg")
    public void reg() {
        test_0.User user = new test_0.User();
        user.setPhone("123");
        user.setName(null);
        //错误调用
        userService.userReg(user);
    }
}

//可以编写如下的错误拦截器
@Service
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgCheckException.class)
    public Map<String， String> handle(
            MethodArgCheckException ex) {

        //print输出结果
        System.out.println("ErrorHandler print test\n");

        MethodHandlerCheckResult methodHandlerCheckResult = ex.checkResult();//函数参数拦截
        List<String> failedInfoList = new ArrayList<>();

        //遍历所有有`@CheckIt` 注解的参数的检测结果
        for (ArgCheckResult argCheckResult : methodHandlerCheckResult.enableArgCheckResultList()) {
            System.out.println(argCheckResult.log());
            System.out.println(argCheckResult.checkArg());
            System.out.println("--------");
            for (LogicCheckResult<?> checkResult : argCheckResult.failedLogicList()) {
                System.out.println(checkResult);
                failedInfoList.add(checkResult.info());
            }
        }

        return Collections.singletonMap("错误信息"， String.join("，"， failedInfoList));
    }
}

```

如果这个时候访问 `/test/zero/reg`

上面编写的错误拦截器`ErrorHandler`会在控制台输出这些信息

```java
ErrorHandler print test

FAILED  arg0 of method:public void test_0.UserService.userReg(test_0.User)
User{phone='123'， address='null'， name='null'}
--------
{exec:'FAILED'， info='手机号码错误: 123'， log='field_path [phone] check by method UserService->phoneCheck(String)'}
```

前端会收到这样的结果

```java
{
    "错误信息": "手机号码错误: 123"
}
```

不借助切面直接使用检测 ，相对切面快很多

```java
@Controller
@RequestMapping(value = "test/zero")
public class TestController {  
    @Autowired
    Purahs purahs;
    @GetMapping(value = "/reg")
    public void reg() {
        test_0.User user = new test_0.User();
        user.setPhone("123");
        user.setName(null);

        CheckResult<Object> checkResult = purahs.checkerOf("example:0[用户信息检测][phone:手机号码检测]").oCheck(user);
        if(checkResult.isFailed()){
            //...自行返回
        }
        userService.userReg(user);
    }
}
```

`purah`可以用不超过一个小时的时间上手， 代码量并不多只有5000行，还有相对完善的2000行单元测试，覆盖主要逻辑

一个有2年以上经验的程序员可以用不到3天的时间完全理清purah的设计逻辑，并快速的进行适合业务的改善扩展

### 代码仓库

[purah-gitee](https://gitee.com/vajval/purah)

[purah-github](https://github.com/vajval/purah)

### 使用方法

```xml
<dependency>  
     <groupId>io.github.vajval.purah</groupId>  
     <artifactId>purah</artifactId>  
     <version>1.0.12</version>  
</dependency>
```

```java
@SpringBootApplication  
@EnablePurah  //启动类上加上这个注解就行
public class ExampleApplication {  
    public static void main(String[] args) {  
      SpringApplication.run(ExampleApplication.class， args);  
    }  
}
```
## purah有以下优点

1. 更简单快捷直观的定义规则及使用，api设计简洁
2. 任意多规则**嵌套组合**，可以用代码动态实现，也支持以配置文件的方式动态配置
3. **支持自定义语法，以不超过注释的复杂度完成对逻辑的控制**
4. 无侵入性，可以与`jsr303`一起使用
5. 控制简单，支持快速失败或成功的动态重排序
6. **扩展性非常之强**
7. 支持上下文缓存，在部分情况下可以节约DDD优化的精力

缺点就是不会再增加太多的功能了，主要精力会放在bug修复和性能提升，以及使api更加简洁

## 一条一条示例

### 1 更简单快捷直观的定义规则及使用

##### 1 基础方法

例如我对用户的姓名进行`中文名字检测`​，我们希望能够像`spring validation`​ 一样在函数参数上加注解来自动实现校验

```java
public class UserService {
    public boolean nameCheck(String name) {//检测用户名是否合规
        return true;
    }
    public boolean nameCheckByUser(User user) {//检测用户名是否合规
        if(user==null)return false;
        return nameCheck(user.getName());
    }
}
```

那么可以这么编写代码，首先将这两个函数作为规则`Checker`​注册

```java
@Autowired
UserService userService;
@Autowired
Purahs purahs;
public void init(){
     LambdaChecker<String> nameChecker = LambdaChecker.of(String.class).build("中文名字检测"， userService::nameCheck);
     LambdaChecker<User> nameByUserChecker = LambdaChecker.of(User.class).build("中文名字检测"， userService::nameCheckByUser);
     purahs.reg(nameChecker);
     purahs.reg(nameByUserChecker);
}
```

然后我们就可以在各种地方使用这个规则，使用方法与`spring validation`相同，失败会抛出`MethodArgCheckException`

```java
@Service
public class TestService{

     // 与spring validation相同的使用方法   失败抛出异常，可进行同一异常处理
     public void test(@CheckIt("中文名字检测") String name){ //执行nameCheck
         //....  
     }
     
     //校验参数类型 自动匹配
     public void test(@CheckIt("中文名字检测") User user){ //执行nameCheckByUser
         //.... 
     }
     
     @FillToMethodResult//将结果填充到函数的返回值中，返回类型只支持boolean或者CheckResult<?>
     public boolean test(@CheckIt("中文名字检测") String name){ //执行nameCheck
         //....  
     }
     
     @Autowired
     Purahs purahs;
     
     //不借助切面 手动检测
     public void test(User user){ //执行purahs
          boolean success = purahs.checkerOf("中文名字检测").oCheck(user).isSuccess();
         //.... 
     }
}
```

也许使用者会觉得这么注册会很麻烦，而且还要考虑注册的时机，其实还有很多方法

##### 2 可以用`@PurahMethodsRegBean` +`ToChecker` 直接将函数转换为 `Checker`并且注册

注册完成后就可以像上面一样使用

```java
@Component
@PurahMethodsRegBean //将有 @ToChecker的函数定义为规则并且注册，不要放的比springboot启动类层级高
public class CheckBean {
     @ToChecker("中文名字检测")
     public boolean nameCheckByName(String name) {
         //逻辑
         return true;
     } 
     @ToChecker("中文名字检测"，autoNull = AutoNull.failed)// user为null，自动返回失败
     public boolean nameCheckByUser(User user) {
         return nameCheckByName(user.getName());
     } 
}
```

`ToChecker`支持通过`@FVal`实现对**不同类的相同业务字段**获取

例如 我们要进行 `手机号归属地检测`

```java
public boolean phoneBelongAddress(String phone，String address) {
      return true;
}
```

而我们有很多类都需要检查这两个字段，比如下面的`User` 和`People`，或者是前端直接传递的`json`， `map`

```java
class User{
   String id;
   @TestAnn("user_phone")
   String phone;
   String address;
}
class People{
   String id;
   @TestAnn("people_phone")
   String phone;
   People child;
}
```

我们可以用`@ToChecker`这么编写

```java
@ToChecker("手机号归属地检测")
public boolean phoneAddress(
      @FVal("phone")String phone，      //phone value
      @FVal("phone")TestAnn TestAnn，   //@TestAnn("user_phone")
      @FVal("address")String address   //address value
){
     //逻辑
    return phoneBelongAddress(phone，address);
}
//然后就可以使用了
@Service
public class TestService{
     //基于反射的字段自动填充
     //@TestAnn("user_phone") 注解自动填充
     public void test(@CheckIt("手机号归属地检测") User user){
     }
     //@TestAnn("people_phone") 注解自动填充
     public void test(@CheckIt("手机号归属地检测") People people){   
     }
     //可以获取对应字段值，无法获得任何注解
     public void test(@CheckIt("手机号归属地检测") Map map){

     }
}
```

也许**相同的业务属性在不同类中`Field`的`name`不同**，也是通过设置`@FVal`中的`matcher` 简单实现上面功能的，不过要看文档了

##### 3 常规手段，通过`spring` 的`@Component`注解自动注册

```java

@Name("中文名字检测")
@Component
public class TestChecker implements Checker<String，String> {
    @Override
    public CheckResult<String> check(InputToCheckerArg<String> arg) {
        List<Annotation> annotationList = arg.annListOnField();
        String name = arg.argValue();
        //......逻辑
        return LogicCheckResult.success();
    }
}
```

##### 4 根据名字动态生成

自定义语法也是靠的这个

```java
@PurahMethodsRegBean
@Component
public class IocMethodRegTestBean {

    private static final String RANGE_MATCH = "value in [*-*]";

    @ToCheckerFactory(match = RANGE_MATCH)
    public Checker<Number， Object> range(String name) {
        String[] split = name.substring(name.indexOf("[") + 1， name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return LambdaChecker.of(Number.class).build(i -> range(i， min， max));
    }
    public boolean range(Number value， double min， double max) {
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }
}
//直接用
public void userReg(@CheckIt("value in [1-3]") Long value)
```

### 2 和 3  任意多种规则嵌套组合动态实现，用自定义语法以不超过注释的复杂度完成对逻辑的控制

##### 1 根据规则名字动态生成checker

基于此直接实现了自定义语法，**自定义语法可以随意扩展**，  
自带了示例用的`example` 语法， 实例用，没有语法错误检测

用法如下，例如我们相对user对象进行`手机号归属地检测`，并且对name字段进行`中文名字检测`​，直接写就能用

```java
public void userReg(@CheckIt("example:1[手机号归属地检测][name:中文名字检测]") User user) //，直接写就能用
```

##### 2 基于配置文件自由组合

编写一个这样的bean填充配置文件参数，

```java
@ConfigurationProperties(value = "purah")
@Configuration
public class PurahConfigPropertiesBean extends PurahConfigProperties {

}
```

再application.yml中编写逻辑

```yml
purah:
  combo_checker:
    - name: 用户注册检查  #起名字
      checkers: 手机号归属地检测  #对入参的检测
      mapping:      #对field的检测
        fixed: #固定匹配    field_matcher的类型名字 除了fixed 外还有很多
          "name": 中文名字检测
        class_name: #field className 匹配
          "java.lang.String": 敏感词检测
        general: #通配符 匹配
          "[*|*.*]": "自定义注解检测" #对一级字段和二级字段进行自定义注解检测，中括号是yaml对特殊字符的要求
     
```

写了可以直接用

```java
public void userReg(@CheckIt("用户注册检查") User user)
```

用PurahIocRegS可以实现热更新，只要保证PurahConfigPropertiesBean中的数据是新的就行

```java
@Component
public class TestCallBack  {
    @Autowired
    Purahs purahs;
    @Autowired
    PurahConfigPropertiesBean purahConfigPropertiesBean;

    public void exec() {
        PurahContext purahContext = purahs.purahContext();
        PurahIocRegS purahIocRegS = new PurahIocRegS(purahContext);
        purahIocRegS.regCheckerByProperties(purahConfigPropertiesBean);//注册并且覆盖旧的
    }

}
```

‍

##### 3 用代码手动控制

默认支持9种`FieldMatcher`类型，其中`FieldMatcher`​也是可以扩展的

```java

@PurahMethodsRegBean
@Component
public class checkBean {
     @Autowired
     Purahs purahs;
     @ToChecker("用户注册检查")
     public Checker<?，?> phoneAddress(){    //用combo打组合拳
         return purahs.combo("手机号归属地检测")  //对user 进行`手机号归属地检测`
                      .match(new FixedMatcher("name")，"中文名字检测") //对名字为name字段进行匹配，并且进行 "中文名字检测"
                      .match(new GeneralFieldMatcher("*|*.*")， "自定义注解检测"); //对一级字段和二级字段进行自定义注解检测
     }
}
public void userReg(@CheckIt("用户注册检查") User user) //直接用
```

### 4和5 控制及重排序

想在purah框架下使用`jsr303`是没有问题的，purah **没有任何**对`jsr303`​的依赖，无论开发者喜欢使用什么版本，都不会有版本冲突问题  
用类似下面的代码实现`jsr303`的逻辑即可

```java
@Name("jsr303")
@Component
public class JSR303Checker<INPUT_ARG， RESULT> implements Checker<INPUT_ARG， RESULT> {
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

一起用没有问题的

```java
public void userReg(@CheckIt("example:1[jsr303,手机号归属地检测][name:中文名字检测]") User user)
```

嫌麻烦可以直接写类上

```java
@CheckIt("example:1[jsr303,手机号归属地检测][name:中文名字检测]")
class User {
  //....
}
//加到类上之后以下两种写法效果一样
public void userReg(@CheckIt User user){
public void userReg(@CheckIt("example:1[jsr303,手机号归属地检测][name:中文名字检测]")User user)
//请注意 只有@CheckIt 中没写值的时候类上的才生效，这个只有 `用户注册检查` 会生效
public void userReg(@CheckIt("用户注册检查")User user)
```

可以通过`autoReOrder`配置**自动重排序**，**只在快速失败和快速成功**的条件下生效

例如下面`autoReOrder` 为100时，每检测了100条数据，就会优先将容易出错的检查放到前面  
默认顺序**1，2，3，4** ;执行一百次后，如果2经常出错，顺序会变成**2，1，3，4**

```java
customAnnCheck = purahs.combo("1")  
                   .match("2"，"check") //match(new GeneralFieldMatcher("*")， "custom_ann_check")  
                   .match("3"，"check") //match(new GeneralFieldMatcher("childUser.name")， "custom_ann_check")  
                   .match("4"，"check") //match(new GeneralFieldMatcher("childUser.phone|childUser.age")， "custom_ann_check")  
                   .mainMode(ExecMode.Main.all_success).autoReOrder(100);
```

`autoReOrder`默认为-1不重排

组合规则的多种执行方式

```java
public class ExecMode {
    public enum Main {
        // 全成功才行，有错不继续 快速失败
        all_success(0)，
        // 全成功才行，有错也要检查完
        all_success_but_must_check_all(1)，
        // 一个就行，不继续 快速成功
        at_least_one(2)，
        // 一个就行，必须检查完
        at_least_one_but_must_check_all(3);
   }
}
```

如果method上有多个参数，可以用`@MethodCheckConfig`​来控制每个参数结果对函数最终结果的影响

```java
    @FillToMethodResult
    @MethodCheckConfig(mainMode = ExecMode.Main.all_success_but_must_check_all)
    public MethodHandlerCheckResult test4(@CheckIt(value = {"have_child"，"have_child2"}，mainMode = ExecMode.Main.at_least_one) People parent0， @CheckIt("have_child") People parent1， @CheckIt People parent2， People parent3， @CheckIt("have_child") People parent4， People parent5， @CheckIt People parent6， @CheckIt("have_child") People parent7) {
        return null;
    }
```

### 6 扩展性非常之强

下面的`purah`​ **都支持**

1 嵌套结构下自定义注解检测

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
    @Range(min = 1， max = 10， errorMsg = "range wrong")
    Long id;
    People people;//这是本公司的class 所以里面的被注解的字段也要检查
}
```

2 嵌套结构下字段`@CheckIt`检测，动态填充规则也支持

```java
class People {
    @CheckIt("${name}")  
    String phone;  
    @CheckIt("${phone}")  
    String name;
}
class User {
    @CheckIt("example:1[][id:id检测]")
    Long id;
    @CheckIt("人员信息检测")
    People people;
}
```

要看了文档才会用，这里写不下，点进git看文档吧

[purah-gitee](https://gitee.com/vajval/purah)

[purah-github](https://github.com/vajval/purah)

### 7 正在优化的上下文缓存

在项目中可能会遇到如下问题

如下所示，为了安全会被检测两次

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
         //为了防止没有检测，先从db检测参数是否合规
          //........
    }
}
```

DDD 优化后的写法，完美实现逻辑，只是如果项目比较大，改的地方会有点多

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
        hhhhService.reg(safeUser);
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

purah的写法，加个注解就完事，在开启基于threadlocal的上下文缓存的情况下，用户检测只会被调用一次

```java
@SpringBootApplication
@EnablePurah(enableCache = true)//全局打开默认false
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class， args);

    }
}
//或者，这个函数调用开始会开启缓存，结束会释放
@MethodCheckConfig(enableCache = true)
public void reg(@CheckIt("用户检测")User user) {
                testService.reg(user);   
}
```

在全局开启的情况下，可以这么写，但是如果结构太复杂就不推荐了，`@CheckIt` 基于切面，不是bean中调用不生效，容易写bug

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

最后，给个star吧，我什么都会做的