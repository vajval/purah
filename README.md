文档 demo 和单元测试正在补全中

使用方法


```java
@SpringBootApplication
@EnablePurah //启动类上加这个注解使之生效
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}

```


## 编写校验规则

```java
@PurahEnableMethods
@EnableOnPurahContext
public class MethodsToCheckersTestBean {
   
    //生成固定逻辑的 检查器
    @Name("非空判断FromTestBean")
    @ToChecker
    public boolean notEmpty(Object o) {
        return o != null;
    }
    //根据不同的需求形成不同的检查器
    @ToCheckerFactory(match = "取值必须在[*-*]之间判断FromTestBean")
    public boolean range(String name, Number value) {

        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }
   //当使用 取值必须在[*-*]之间判断FromTestBean 规则时
   // 会根据需要检测对象的class 不同， 自动调用不同的函数

    @ToCheckerFactory(match = "取值必须在[*-*]之间判断FromTestBean")
    public CheckerResult range2(String name, int value) {

        String[] split = name.substring(name.indexOf("[") + 1, name.indexOf("]")).split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        boolean success = value.doubleValue() >= min && value.doubleValue() <= max;

        if (success) {
            return SingleCheckerResult.success();
        }
        return SingleCheckerResult.failed(null, name + "取值错误" + value);
    }
}
  
```

然后就可以使用啦

```java
@Service
public class TestService {

    //不符合规则时会抛出异常
    public void voidCheck(@CheckIt("非空判断FromTestBean") CustomUser customUser) {

    }
    // 将结果填充到返回信息里
    @FillToMethodResult
    public boolean checkBoolTest(@CheckIt("非空判断FromTestBean") CustomUser customUser) {
        return false;
    }

    // 如果你自定义了返回内容的话，会将自定义的内容一并返回
    @FillToMethodResult
    public CheckerResult checkResult(@CheckIt("非空判断FromTestBean") CustomUser customUser) {
        return null;
    }
    // 支持根据通配符匹配
    @FillToMethodResult
    public boolean checkResult(@CheckIt("取值必须在[*-*]之间判断FromTestBean") int num) {
        return false;
    }

  
}

```

### 支持通过配置文件生成较为复杂的规则

```yaml
   - name: 贷款申请  ## 定义检查其字段
      mapping:
        wild_card: ## 通配符匹配器
          "[num_*]" : 取值范围检测
        class_name: ## 字段class匹配器 请打上全类名
           java.lang.String: 敏感词检测
        type_by_ann: ## 类中字段注解匹配器
          "[短文本]" : 敏感词检查
          "[长文本]" : 敏感词检查
        re:    ## 字段正则表达式匹配器
          "[.*]": A,B
        general: ##推荐的通用匹配器，支持多级嵌套的通配符匹配器
          "[msg*.*num]": 数字必须在[1-10]之间
        
## 上面为自带的，下面的是可以自定义的 ，可以随意扩展，
## 支持 
        product_city_rate_wild_card:
          "[{直辖市}_rate}]": 直辖市利率标准检测
          "[{一线城市}_rate}]": 一线城市市利率标准检测
          "[{北方城市}_rate}]": 北方城市市利率标准检测
```

## 自定义语法

觉得写配置文件麻烦的可以自定义语法

例如通过example 中的 CustomSyntaxChecker类

实现支持 @CheckIt("`example:1[][*:自定义注解检测;*.*:自定义注解检测]`")

来实现对对象所有字段 及其嵌套对象的 所有字段的自定义注解检测

```java
  /*
     * 自定义语法 详情见  CustomSyntaxChecker
     * 实现 所有字段及子对象自定义注解检测,即使出错也会检测所内容
     *
     * 相当于配置文件中
     * - name: "example:1[][*:自定义注解检测;*.*:自定义注解检测]"
     *      exec_type: 1
     *      mapping:
     *         general:
     *           "[*]": 自定义注解检测
     *           "[*.*]": 自定义注解检测
     * * 匹配
     * id
     * name
     * phone
     * age
     * childCustomUser
     * *.* 匹配
     * childCustomUser.id
     * childCustomUser.name
     * childCus
     * tomUser.phone
     * childCustomUser.age
     * childCustomUser.childCustomUser
     *
     */
    @FillToMethodResult
    public CheckerResult checkByCustomSyntaxWithMultiLevel(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") CustomUser customUser) {
        return null;
    }
```

## 自定义注解检测

对于`自定义注解检测`  的实现方法 可以通过继承 `AbstractCustomAnnChecker`类来简单的实现

直接编写对应函数即可实现检测需求

### 要求
第一个参数 必须是自定义的注解
第二个 是需要检查的参数
返回类型 必须是 CheckerResult或者boolean

需要检查的参数可以用 CheckInstance 封装，用CheckInstance 封装的参数可以获得部分上下文




CheckerResult 中可以携带自定义的返回信息 ，boolean 的不行

通过继承AbstractCustomAnnChecker 类来实现 `自定义注解检测` 的函数 

想扩展的话 可以重写 initMethod函数来支持更多


```java
@Name("自定义注解检测")  //自定义checker一定要加一个名字
@EnableOnPurahContext//启用
@Component// 放到spring里
//下面的函数 会根据 AbstractCustomAnnChecker 类的方法注册为检测器
public class CustomAnnChecker extends AbstractCustomAnnChecker {

    public boolean notNull(NotNull notNull, Integer age) {
        if (age == null) {
            return false;
        }
        return true;


    }

    public CheckerResult cnPhoneNum(CNPhoneNum cnPhoneNum, CheckInstance<String> str) {
        String strValue = str.instance();

        //gpt小姐 说的
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return success("正确的");
        }
        return SingleCheckerResult.failed(str.instance(), str.fieldStr() + ":" + cnPhoneNum.errorMsg());


    }

    public CheckerResult notEmpty(NotEmpty notEmpty, CheckInstance<String> str) {
        String strValue = str.instance();
        if (StringUtils.hasText(strValue)) {
            return success("正确的");
        }
        return SingleCheckerResult.failed(str.instance(), str.fieldStr() + ":" + notEmpty.errorMsg());


    }


    public CheckerResult range(Range range, CheckInstance<Number> num) {
        Number numValue = num.instance();
        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
            return SingleCheckerResult.failed(num.instance(), (num.fieldStr() + ":" + range.errorMsg()));
        }
        return success("参数合规");

    }


}

```

## 自定义规则的字段匹配器


在 core 的 com.purah.matcher下面 有几个自带的匹配器

使用请看具体类上的 Name注解



### wildCardMatcher

支持 通配符匹配字段

使用请在配置文件中写 wild_card

a. 会匹配 ab ac 不会匹配abc

a* 会匹配 ab ac abc 不会匹配ba


### reMatcher

为使用正则表达式匹配字段

使用请在配置文件中写 re

请注意在 yml文件下 正则表达式的写法


### AnnTypeFieldMatcher

使用请在配置文件中写 type_by_ann

```java
@FieldType("短文本")
String smallText;

@FieldType("长文本")
String longText;
```

