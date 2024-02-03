purah 是一个参数检验框架

## 使用方法
```java
   @PostMapping
public void postBlog(@CheckIt("博客发表检测") Blog blog) {

        /*
         * 逻辑什么的
         */
        }

```


## 规则创建方法
### 自己编写逻辑方法

### 通过配置文件，将规则组合


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
组合用的基本规则必须是被实现的
两种实现方法

## 利用factory 根据规则名字自动生成逻辑

以城市贷款利率为例子

```java
@EnableOnPurahContext // 必须加 使这个 检测器生效，不加不生效
@Component //在 springboot 环境下使用必须加 ， 自动装配 ，可以读数据库之类的
public class CityRateChecker2 implements EasyCheckFactory<Double> {

    // 规则名字是否匹配 生成 规则
    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.endsWith("利率标准检测");
    }

    @Override
    public Predicate<Double> predicate(String needMatchCheckerName) {
        double min = 0.1;
        double max;
        if (needMatchCheckerName.startsWith("直辖市")) {
            max = 0.5;
        } else if (needMatchCheckerName.startsWith("一线城市")) {
            max = 0.4;
        } else if (needMatchCheckerName.startsWith("北方城市")) {
            max = 0.3;
        } else {
            max = 0.2;
        }
        return (value) -> value > min && value < max;
    }

    
}

```
希望自定义异常消息内容时时可以这么写，不一定非得是 string 消息
```java

@EnableOnPurahContext
@Component
public class CityRateChecker implements CheckerFactory {

    @Override
    public boolean match(String needMatchCheckerName) {
        return needMatchCheckerName.endsWith("利率标准检测");
    }

    @Override
    public Checker createChecker(String needMatchCheckerName) {
        double min = 0.1;
        double max;
        if (needMatchCheckerName.startsWith("直辖市")) {
            max = 0.5;
        } else if (needMatchCheckerName.startsWith("一线城市")) {
            max = 0.4;
        } else if (needMatchCheckerName.startsWith("北方城市")) {
            max = 0.3;
        } else {
            max = 0.2;
        }
        double finalMax = max;
        return new BaseChecker<Double, Object>() {
            @Override
            public CheckerResult doCheck(CheckInstance<Double> checkInstance) {
                Double rate = checkInstance.instance();
                if (rate < min) {
                    return failed("利率值过小为 " + rate);
                }
                if (rate > finalMax) {
                    return failed("利率值过大为 " + rate);
                }
                return success("利率值正合适");
            }
        };

    }
```
## 编写固定逻辑
有耐心可以这么写
```java

```

### 自定义规则的字段匹配器
在 core 的 com.purah.matcher下面 有几个自带的匹配器

使用请看具体类上的 Name注解

### wildCardMatcher
支持 通配符匹配字段 

使用请在配置文件中写 wild_card

a. 会匹配 ab  ac 不会匹配abc

a* 会匹配 ab  ac abc 不会匹配ba

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











