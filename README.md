# purah

Here is the translation provided by miss gpt
[中文文档](https://github.com/vajval/purah/blob/master/README_ZH.md)

It is probably the simplest and most user-friendly Java parameter validation framework so far. **It basically implements rule control with the complexity of annotation level,**  omitting as much as possible parts other than the necessary validation logic.

Usage is similar to Spring Validation, you can use it by adding the @CheckIt annotation on method parameters.

However, compared to Spring Validation's way of controlling logic with groups, Purah's usage of logic is much simpler.

**Simplicity is the result, not an option.**

There might be some bugs to be discovered, and some missing unit tests and documentation, which will be gradually fixed and supplemented.

### 0 Usage

Maven dependency

```xml
<dependency>
    <groupId>io.github.vajval.purah</groupId>
    <artifactId>purah</artifactId>
    <version>1.0.4-beta</version>
</dependency>
```

Add annotation on the startup class

```java
@SpringBootApplication
@EnablePurah(checkItAspect = true) // Add this annotation, checkItAspect is true by default, set to false to disable aspect validation
public class ExampleApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

### 1 Basic Usage

For example, to perform Chinese name detection on input names, first define the necessary logic

```java
@PurahMethodsRegBean // Register functions with annotations in the bean as rules
public class checkBean {
    @ToChecker("Chinese name check") // Convert the function to a rule and register it
    public boolean nameCheck(String name) {
        //......
        return true;
    }
}
```

Then it can be used like Spring Validation, with the added @FillToMethodResult annotation to fill the validation result into the return value

* Without the `@FillToMethodResult`​ annotation, a `MethodArgCheckException`​ exception will be thrown on failure
* With the `@FillToMethodResult`​ annotation, the validation result will be saved in the return value. The return type needs to satisfy `boolean`​, `CheckResult`​, or `MethodHandlerCheckResult`​

```java
public void test(@CheckIt("Chinese name check") String name) {  // On failure, throw MethodArgCheckException

}
@FillToMethodResult
public boolean testB(@CheckIt("Chinese name check") String name) {   // Fill the isSuccess() boolean result of CheckResult into the return value
    return false;
}
@FillToMethodResult
public CheckResult testC(@CheckIt("Chinese name check") String name) {   // Fill the CheckResult into the return value
    return null;
}
```

It also supports getting the return result directly without using aspects

```java
@Autowired
Purahs purahs;
public void test(String name) {  
   CheckResult checkResult = purahs.checkerOf("Chinese name check").check(name); // Manually check without using aspects
}
```
`@ToChecker` comes with an automatic null check

```java
public @interface ToChecker {

    String value();

    AutoNull autoNull() default AutoNull.notEnable;

}
```

It can automatically succeed or fail when the parameter is null. The default is `notEnable`, which means no automatic handling of null values.

```java
    @ToChecker(value = "auto_null_success", autoNull = AutoNull.success) // When name is null, automatically returns success
    public boolean auto_null_success(String name) {
        return false;
    }

    @ToChecker(value = "auto_null_failed", autoNull = AutoNull.failed) // When name is null, automatically returns failure
    public boolean auto_null_failed(String name) {
        return true;
    }
```

### 2 Multi-field Joint Validation

If you want to validate multiple fields together

For example, check if the phone number matches the address

```java
class People {
   String phone;
   String address;
   String id;
   People(String phone, String address, String id) {
       this.phone = phone;
       this.address = address;
       this.id = id;
   }
}

@PurahMethodsRegBean
public class CheckBean {
    @ToChecker("Phone and address match check")
    public boolean checkPhoneAndAddress(People people) {
        // Implement your logic here
        return people.phone.equals(people.address); // Example logic, replace with actual check
    }
}

// Usage
public void test(@CheckIt("Phone and address match check") People people) {
    // Validation logic will be applied here
}
```

At this point, the call cannot be distinguished because it supports all types by default, i.e., `Object`​

```java
public void testCheck(@CheckIt("test") User user); // testUser or testPeople
public void testCheck(@CheckIt("test") People people); // testUser or testPeople
```

To distinguish, you need to explicitly add parameters that fill the root object value on the function

```java
@ToChecker("test")
public boolean testUser(
    // Adding this parameter can limit the type while filling the root object value; without it, all types are supported by default, i.e., Object
    @FVal(FieldMatcher.rootField) User user,
    @FVal("phone") String phone, // phone value
    @FVal("id") String id // address value
) {
    //......
    return true;
}
@ToChecker("test")
public boolean testPeople(
    @FVal(FieldMatcher.rootField) People people,
    @FVal("address") String address, // phone value
    @FVal("name") String name // address value
) {
    //......
    return true;
}
public void testCheck(@CheckIt("test") User user); // Executes testUser
public void testCheck(@CheckIt("test") People people); // Executes testPeople
```

### 3 Combining Multiple Rules for Validation

Purah supports combining **any number** of rules into new rules and also supports validating values of specific Fields according to specified rules.

For example, during user registration, you might validate the `name`​ of the user with a `Chinese name check`​ and the user with a `phone number location check`​.

These combined checks can be named `User Registration Check`​.

There are three methods to achieve this.

##### Method One

###### Definition

```java
    @PurahMethodsRegBean
    public class CheckBean {
        @Autowired
        Purahs purahs;
        @ToChecker("User Registration Check")
        public Checker<?,?> phoneAddress() {    // Combine multiple checks
            return purahs.combo("Phone Number Location Check")  // Validate the user with `Phone Number Location Check`
                      .match(new GeneralFieldMatcher("name"), "Chinese Name Check") // Match the field with name 'name' and perform "Chinese Name Check"
                       // GeneralFieldMatcher supports multi-level matching like a.b.c
                       // It also supports multi-level matching with list parameters like childList#10.b.name
                       // It also supports wildcards like *, child.*, *.name, *.na?e
                       // It can simultaneously match multiple patterns like "a.b.c|childList#10.b.name|*|*.name|*.na?e"
                       // Besides GeneralFieldMatcher, many others are supported, and they can be customized as well
                      .mainMode(ExecMode.Main.all_success); // ExecMode.Main explained below
        }
    }

```

###### Usage

```java
   public void register(@CheckIt("User Registration Check") User user) {
   }
```

For multiple rule execution methods (ExecMode.Main), different types can be selected to control the judgment logic.

```java
public class ExecMode {
    // Checks that are ignored are neither considered successful nor failed; they are treated as not checked and do not participate in composite judgments.
    public enum Main {
        // All must be successful, stop on error = Fast Fail
        all_success(0), // Default value
        // All must be successful, but check all even if there are errors
        all_success_but_must_check_all(1),
        // At least one must be successful, stop on error
        at_least_one(2),
        // At least one must be successful, but check all even if there are errors
        at_least_one_but_must_check_all(3);

//......
```

##### Method Two (Recommended)

This project supports custom syntax, allowing you to specify logic in a way that is no more complex than comments.

Purah provides an example with the `example:`​ syntax.

Below is a direct implementation of the previous effect.

```java
    // The details of the example syntax implementation can be found in `ExampleCustomSyntaxCheckerFactory`
    // 1 is all_success_but_must_check_all in ExecMode.Main
    public void userReg(@CheckIt("example:1[Phone Number Location Check][name:Chinese Name Check]") User user) {
     // The first bracket contains checks for the object itself
     // The second bracket contains checks for specific fields of the object, with default support for simple wildcards like abc*, abc?
    }
```

Implementing custom syntax is not complex. Here is the implementation of `example:`​. To use it practically, you need to add logic to check whether the syntax is valid.

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

##### Method Three: Write in application.yml

Register this bean with Spring Boot.

```java
@ConfigurationProperties(value = "customProperties123")
@Configuration
public class PurahConfigPropertiesBean extends PurahConfigProperties {

}
```

```yml
customProperties123:
  combo_checker:
    - name: user_reg
      checkers: abc
      mapping:
        general: # Type name of field_matcher, besides general there are many others
          "[address|parent_address]": national_check
          "[*name*]": name_validity_check
          "[age]": age_check
        class_name:
          "java.lang.String": sensitive_word_check
    - name: user_reg_and_phone_and_child
      checkers: user_reg
      mapping:
        wild_card:
          "[phone]": phone_check
          "[child]": user_reg
```

###### Usage

```java
   public void register(@CheckIt("user_reg_and_phone_and_child") User user) {
   }
```

Purah has a callback function that is called when the container is refreshed (i.e., when the `ContextRefreshedEvent`​ is received). Implement this interface and register it with Spring for hot updates.

You can also use scripting languages directly; the approach is flexible.

```java
public interface PurahRefreshCallBack {
    void exec(Purahs purahs);
}

```

If your detection function is written like this, and `ResultLevel`​ is set to all,

```java
    @PurahMethodsRegBean
    public class CheckBean {
        @Autowired
        Purahs purahs;
        @ToChecker("Phone Number Location Check")
        public CheckResult<?> phoneAddress(
            @FVal("phone") String phone, // Phone value
            @FVal("phone") TestAnn testAnn, // Annotation on the phone field, People is null, User is @TestAnn("123")
            @FVal("address") String address // Address value
        ) {   
               return LogicCheckResult.success(null, "Phone number location is very correct");
        }
        @ToChecker("Chinese Name Check") // Convert the function to a rule and register it
        public CheckResult<?> nameCheck(String name) {
        //......
               return LogicCheckResult.success(null, "Chinese name is very correct");
        }
   }
```

The return format for the combined rules is as follows:

```java
MultiCheckResult{base={"execInfo":"SUCCESS","log":"SUCCESS ([]: null)"}, valueList=[{"execInfo":"SUCCESS","log":"Phone number location is very correct"}, {"execInfo":"SUCCESS","log":"Chinese name is very correct"}]}
```

It can be nested several layers deep.

If both `id`​ and `name`​ are checked with `check1`​ and `check2`​, it will return results like this:

```json
{   // MultiCheckResult
  "main": "success: 'id|name':'check1,check2'",
  "valueList": [
    {  // MultiCheckResult
      "main": "success: 'id':'check1,check2'",
      "valueList": [{"logic": "success:'id':check1"},{"logic": "success:'id':check2"}] // LogicCheckResult
    },
    {
      "main": "success: 'name':'check1,check2'",
      "valueList": [{"logic": "success:'name':check1"}, {"logic": "success:'name':check2"}]
    }
  ]
}
```
For `@ToChecker`, there is also an `ignore` option. The checkResult that is ignored is **not** considered successful nor **considered** a failure.

It is considered **non-existent**, equivalent to not being checked, and does not affect the final composite result.
```java
    @ToChecker(value = "auto_null_ignore", autoNull = AutoNull.ignore)
    public boolean auto_null_ignore(String name) {
        return false;
    }

    @ToChecker(value = "auto_null_failed", autoNull = AutoNull.failed)
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

    Assertions.assertFalse(purahs.checkerOf("auto_null_ignore_combo_failed").check(null)); // Result is false
    Assertions.assertTrue(purahs.checkerOf("auto_null_ignore_combo").check(null)); // Result is true
```
### 4 Type Auto-Matching

We need to check if a user is at least 18 years old during registration, where the input might be a `User`​ object or just the `age`​ itself.

```java
    class User {
       String phone;
       String address;
       String name;
       int age;
    }
```

###### Definition

```java
    @PurahMethodsRegBean
    public class CheckBean {
        @ToChecker("Age Validity Check")
        public boolean ageCheckByUser(User user) {
            return user.age >= 18;
        }
        @ToChecker("Age Validity Check")
        public boolean ageCheckByInt(int age) {
            return age >= 18;
        }
    }
```

You can use it directly:

```java
    public void userReg(@CheckIt("Age Validity Check") User user) { // Executes ageCheckByUser
    }
    public void userReg(@CheckIt("example:1[][age:Age Validity Check]") User user) { // Executes ageCheckByInt on the user's age field
    }
    public void userReg(@CheckIt("Age Validity Check") int age) { // Executes ageCheckByInt
    }
```

For scenarios involving multi-field joint validation:

```java
 class User {
       String phone;
       String id;
 }
 class People {
       String address;
       String name;
 }

@ToChecker("test") 
public boolean testUser(
                @FVal("phone") String phone, // phone value
                @FVal("id") String id // id value 
) {
            //......
            return true;
}
@ToChecker("test") 
public boolean testPeople(
                @FVal("address") String address, // address value
                @FVal("name") String name // name value 
) {
            //......
            return true;
}
```

At this point, calling the functions cannot be distinguished, as they default to supporting all types, i.e., `Object`​.

```java
public void testCheck(@CheckIt("test") User user); // testUser or testPeople
public void testCheck(@CheckIt("test") People people); // testUser or testPeople
```

To distinguish them, you need to explicitly add parameters for filling the root object on the function.

```java
@ToChecker("test") 
public boolean testUser(
               // Adding this parameter allows filling the root object value and limits the type. Without it, all types (Object) are supported.
                @FVal(FieldMatcher.rootField) User user,
                @FVal("phone") String phone, // phone value
                @FVal("id") String id // id value 
) {
            //......
            return true;
}
@ToChecker("test") 
public boolean testPeople(
                @FVal(FieldMatcher.rootField) People people,
                @FVal("address") String address, // address value
                @FVal("name") String name // name value 
) {
            //......
            return true;
}
public void testCheck(@CheckIt("test") User user); // Executes testUser
public void testCheck(@CheckIt("test") People people); // Executes testPeople
```

### 5 Purah Basic Rules and JSR303

1. If you want to use Purah but are worried about aspect conflicts with existing code, you can disable the aspect with `@EnablePurah(checkItAspect = false)`. This will turn off the aspect. Apart from this, only `io.github.vajval.purah.spring.ioc.RegOnContextRefresh` will reload the `PurahContext bean` during the `ContextRefreshedEvent`. Unless you manually register non-compliant checkers or FieldMatchers in the `PurahContext` during container refresh, leading to exceptions, disabling the aspect should not impact your project. **If it does cause any issues, it will be considered a bug and will be fixed.**

2. We need to perform `User Registration Check` and `Age Legality Check` during user registration.

   Directly implement it as follows:

    ```java
    public void userReg(@CheckIt("example:1[User Registration Check][age:Age Legality Check]") User user) {
    }
    ```

   If we want to validate the user object in every function that uses it, we can add annotations to each function parameter. However, this can become cumbersome.

   **So, we can directly add it to the class**:

    ```java
    @CheckIt("example:1[User Registration Check][age:Age Legality Check]")
    class User {
      //...
    }
    // After adding to the class, the following two methods have the same effect
    public void userReg(@CheckIt User user) {
    public void userReg(@CheckIt("example:1[User Registration Check][age:Age Legality Check]") User user)
    // Note: The class-level annotation only takes effect when the @CheckIt annotation on the parameter does not have a value. This will only apply the `User Registration Check`.
    public void userReg(@CheckIt("User Registration Check") User user)
    ```

3. About **JSR303**

   If you want to use JSR303 validation within Purah:

   Note that **Purah does not have any dependencies related to JSR303. Users need to choose the desired version of dependencies and implement code similar to the following**:

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
            // Convert to CheckResult as needed
        }
    }
    ```

   This can then be used as follows:

    ```java
    public void userReg(@CheckIt("jsr303") User user) {
    }
    ```

4. You might expect `@CheckIt` to handle all fields in a nested manner like JSR303, but this is not supported by default due to its granularity. You can enable this through **point 8: Nested Structure Multi-level Field CheckIt Detection** (see below).

    ```java
    class User {
        @CheckIt("ID Check")    // Not effective
        Long id;
        @CheckIt("Name Check")  // Not effective
        String name;
        @CheckIt("${id}")       // Not effective
        String address;
    ```


### 6 Custom Annotation Validation

If you want to customize annotations on fields and then perform custom annotation validation on all fields when receiving objects

Note that these annotations are defined by yourself in unit tests and are not limited to jsr303. You can write them freely

```java
class User {
    @Range(min = 1, max = 10, errorMsg = "range wrong")
    public Long id;
    @NotEmptyTest(errorMsg = "this field cannot be empty")
    public String name;
    @CNPhoneNum(errorMsg = "phone number wrong")
    public String phone;
    @NotNull(errorMsg = "cannot be null")
    public Integer age;
}
```

Steps to validate all fields

For example, not limited to the method below

1. Write functions that can handle annotations and values
2. Write a checker class that extends CustomAnnChecker and add the `@Name("Custom Annotation Check")`​ annotation to this class
3. Add the functions to the checker class according to the specified format, as many as needed

**Specified Format**

  The first parameter is the custom annotation, the second is the parameter to be checked, which can be wrapped in InputToCheckerArg. InputToCheckerArg contains annotation and field information.

  The format is specified by the inherited `CustomAnnChecker`​. If you find it inadequate, you can write a new one. By opening `CustomAnnChecker`​, you will find it does not require much logic.

```java
@Name("Custom Annotation Check")
@Component
public class MyCustomAnnChecker extends CustomAnnChecker {
    public MyCustomAnnChecker() {
        super(ExecMode.Main.all_success, ResultLevel.only_failed_only_base_logic);
    }
    // The following 3 detection functions will be automatically matched and take effect
    public CheckResult<?> cnPhoneNum(CNPhoneNum cnPhoneNum, InputToCheckerArg<String> str) {
        String strValue = str.argValue();
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return LogicCheckResult.successBuildLog(str, "Correct");
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
        return LogicCheckResult.successBuildLog(num, "Parameter compliant");
    }
}
```

‍

###### Usage

The example requirement can be easily achieved using custom syntax, `*`​ is a wildcard meaning all fields

‍

```java
    // If you want to perform custom annotation validation on fields like aId, bId, just change `*` to `*Id`
    public void userReg(@CheckIt("example:1[][*:Custom Annotation Check]") User user) {  

    }
    // Or
    public void reg(User user) {
        MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
                .match(new GeneralFieldMatcher("*"), "Custom Annotation Check")
                .check(user);
    }

```

There might be cases where the same annotation is placed on fields of different classes

```java
    @CNPhoneNum(errorMsg = "phone number wrong")
    public String phone;
    @CNPhoneNum(errorMsg = "phone number wrong")
    public Long phone;
```

Then you need to write the following two functions, they will be automatically matched by type, just write them and nothing else

```java
    public boolean cnPhoneNum(CNPhoneNum cnPhoneNum, Long value) {
        return cnPhoneNum(cnPhoneNum, value.toString()).isSuccess();
    }
    public CheckResult<?> cnPhoneNum(CNPhoneNum cnPhoneNum, String strValue) {
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return LogicCheckResult.successBuildLog(str, "Correct");
        }
        return LogicCheckResult.failed(str.argValue(), str.fieldPath() + ":" + cnPhoneNum.errorMsg());
    }

```

### 7 Custom Annotation Checking for Complex Nested Structures

Projects often require that nested structures of objects are checked for annotated fields. Even though the nesting can be very deep, typically only the classes written by the project developers need to be inspected for annotated fields.

For example, given a `User`​ object, we clearly only want to perform nested checks on the `People`​ field. Types like `Long`​ and `String`​ that are not our custom classes usually do not need to be checked for annotated fields.

```java
package org.MyCompany;

class People {
    @CNPhoneNum(errorMsg = "phone number wrong")
    String phone;
    @NotEmptyTest(errorMsg = "this field cannot be empty")
    String name;
}
package org.MyCompany;

class User {
    @Range(min = 1, max = 10, errorMsg = "range wrong")
    Long id;

    People people; // This is our company's class, so we need to check the annotated fields inside it.
}
```

Purah provides an `AnnByPackageMatcher`​ that allows specifying the packages of classes to be checked for nested annotated fields. Classes that do not meet the criteria, like `java.lang.*`​, will not be checked.

Implement the `needBeCollected(Field field)`​ function to determine which fields' values need to be collected for checking.

```java
public void reg(User user) {
    // Perform nested checks on fields in packages matching org.MyCompany.* and org.MyCompany2.*
    AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("org.MyCompany.*|org.MyCompany2.*") {
        @Override
        protected boolean needBeCollected(Field field) {
            // Collect values for checking if the field has the required annotations
            Set<Class<? extends Annotation>> annotationSet = Sets.newHashSet(Range.class, CNPhoneNum.class, NotEmptyTest.class);
            for (Class<? extends Annotation> aClass : annotationSet) {
                if (field.getDeclaredAnnotationsByType(aClass) != null) {
                    return true;
                }
            }
            return false;
        }
    };
    // Collect and check fields id, people.phone, and people.name. The people field itself is not annotated, so it won't be collected.
    // Execute
    MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
            .match(annByPackageMatcher, "Custom Annotation Check")
            .check(user);
}
```

If the hierarchy is only two levels deep, it's simple—just use `*|*.*`​. For three levels, use `*|*.*|*.*.*`​, or write it explicitly if the matching results are known.

```java
// The following four approaches achieve the same effect as above
public void userReg(
        @CheckIt("example:1[][*|*.*:Custom Annotation Check]") User user) {
}
public void userReg(
        @CheckIt("example:1[][id|people.phone|people.name:Custom Annotation Check]") User user) {
}

public void reg(User user) {
    MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
            .match(new GeneralFieldMatcher("*|*.*"), "Custom Annotation Check")
            .check(user);
}

@CheckIt("example:1[][id|people.phone|people.name:Custom Annotation Check]")
class User {
    //....
}
public void userReg(@CheckIt User user) {
}
```

If the logic of `FieldMatcher`​ is considered complex and impacts performance, caching can be enabled to improve performance.

**With caching enabled, the logic inside** **​`FieldMatcher`​**​ **is only executed once.**

However, caching should only be used when the fields being checked do not change based on the input.

There are four common types of `FieldMatcher`​ that support multiple levels:

1. ​`FixedMatcher("people.name|people.phone|noExist")`​ returns a map where the value of `noExist`​ is null, which will be checked by the checker. Supports caching.
2. ​`NormalMatcher("people.name|people.phone|noExist")`​ returns a map without the `noExist`​ field. This field won't be checked. If `people`​ is null, `people.phone`​ won't be checked either. Supports caching if it doesn't involve multi-level access.
3. ​`GeneralFieldMatcher("*.name|people.phone|noExist")`​ treats `noExist`​ and `people.phone`​ like `FixedMatcher`​, while `*.name`​ is treated like `NormalMatcher`​. Whether it supports caching depends on the situation.
4. ​`AnnByPackageMatcher`​, as shown in the example above, supports caching by default.

However, there are three situations where caching won't be enabled even if `FieldMatcher`​ supports it:

1. If caching is disabled via `@EnablePurah(argResolverFastInvokeCache = false)`​.
2. Complex hierarchical structures where the class cannot be determined when a field is null, so the class of all fields cannot be fixed. For example:

```java
class People {
    @Test("id") String id;
    @Test("child") People child;
    @Test("child") List<People> childList;
    @Test("child") SuperPeople superChild;
}

class final SuperPeople extends People {
    @Test("superId") String id; // annotation change
}

// FixedMatcher("id|child") supports caching
// FixedMatcher("id|child.id") does not support caching
// FixedMatcher("id|superChild.id") supports caching
// Because the class cannot be determined when null, caching is not supported.
// Conversely, if the field's class is final and cannot be inherited, caching can be enabled for null values as well.
// todo: Optimization needed to support non-final fields.
```

3. List access does not support caching, such as `FixedMatcher("childList#100.id")`​, because the list length is not fixed.

Purah is highly extensible, and `FieldMatcher`​ can be easily added, as shown here. Note that the package is scanned from the start-up class directory downwards, so don't place it higher than the start-up class.

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
    public static class Match {
        public static String reverse = ReverseStringMatcher.NAME; // Easy to locate this way
    }
    public static void main(String[] args) {
        String reverseMatcherName = PurahUtils.Match.reverse;
    }
}
```

### 8 Nested Structure Multi-Level FieldCheckit Detection

In the project, you may want to perform the following checks:

```java
class People {
    @CheckIt("Phone Check")
    String phone;
    @CheckIt("Name Check")
    String name;
}

class User {
    @CheckIt("ID Check")
    Long id;
    @CheckIt("Personal Information Check")
    People people;
}
```

This approach is a good idea, but if supported by default, the granularity is too large. Please manually write it in the "Custom Annotation Check" class.

The parts already written in `CustomAnnChecker`​ are commented out; just paste them into `MyCustomAnnChecker`​.

###### Definition

```java
// Definition
@Name("Custom Annotation Check")
@Component
public class MyCustomAnnChecker extends CustomAnnChecker {
    @Autowired
    Purahs purahs;

    // Implement the check for the custom annotation CheckIt
    public MultiCheckResult<CheckResult<?>> checkItAnn(CheckIt checkIt, InputToCheckerArg<Object> inputToCheckerArg) {
        Purahs purahs = purahs();
        String[] checkerNames = checkIt.value();
        ExecMode.Main checkItMainMode = checkIt.mainMode();
        ResultLevel checkItResultLevel = checkIt.resultLevel();
        ComboBuilderChecker checker = purahs.combo(checkerNames).resultLevel(checkItResultLevel).mainMode(checkItMainMode);
        return checker.check(inputToCheckerArg);
    }

    public Purahs purahs() {
        return purahs;
    }

    // Add functions here to make other annotations effective
    public CheckResult<?> cnPhoneNum(CNPhoneNum cnPhoneNum, InputToCheckerArg<String> str) {
        String strValue = str.argValue();
        // GPT小姐 says
        if (strValue.matches("^1[3456789]\\d{9}$")) {
            return LogicCheckResult.successBuildLog(str, "Correct");
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
        return LogicCheckResult.successBuildLog(num, "Parameter Compliant");
    }
}
```

###### Usage

```java
// Usage
public void userReg(@CheckIt("example:1[][id|people.phone|people.name|people:Custom Annotation Check]") User user) {
}
```

You may want `checkIt`​ to support SpEL or other syntax:

```java
class People {
    @CheckIt("${name}")
    String phone;
    @CheckIt("${phone}")
    String name;
}

class User {
    @CheckIt("ID Check")
    Long id;
    @CheckIt("Personal Information Check")
    People people;
}
```

You can write a base class like this; just copy it and you don't need to worry about it:

```java
public abstract class AllFieldCheckItSpelChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {

    final ExecMode.Main mainExecType;
    final ResultLevel resultLevel;

    public AllFieldCheckItSpelChecker(ExecMode.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;
    }

    protected abstract Purahs purahs();
    protected abstract String spel(String value, Map<String, ?> map, InputToCheckerArg<Object> inputToCheckerArg);
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
            String[] array = (String[]) Arrays.stream(checkIt.value()).map(i -> spel(i, map, inputToCheckerArg)).toArray();
            ComboBuilderChecker combo = purahs.combo(array).mainMode(checkIt.mainMode()).resultLevel(checkIt.resultLevel());
            multiCheckerExecutor.add(() -> combo.check(value));
        }
        String log = inputToCheckerArg.fieldPath() + "  " + this.name();
        return multiCheckerExecutor.toMultiCheckResult(log);
    }
}
```

Then you can use it freely.

You can write a checker like this:

```java
@Component
@Name("Fill and Check All Field CheckIt Annotations")
public class TestChecker extends AllFieldCheckItSpelChecker {
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
    protected String spel(String value, Map<String, ?> map, InputToCheckerArg<Object> inputToCheckerArg) {
        // Fill as you want to match
        return null;
    }

    @Override
    protected FieldMatcher fieldMatcher(InputToCheckerArg<Object> inputToCheckerArg) {
        // Match whatever fields you want; for multi-level, use `AnnByPackageMatcher`
        // If it's too cumbersome, just `new GeneralFieldMatcher(*|*.*|*.*.*|*.*.*.*|*.*.*.*.*)`
        return null;
    }
}
```

Note that the parameter of this checker is the root object itself; the custom syntax should be used like this:

```java
public void userReg(@CheckIt("example:1[Fill and Check All Field CheckIt Annotations][id|people.phone|people.name|people:Custom Annotation Check]")
               User user) {
}
```

9 Context Cache

In the project, you may encounter the following issue:

As shown below, for safety, it will be checked twice:

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
        // First check parameters for compliance from the DB
        hhhhService.reg(user);
        //........
    }
}

public class HHHHService {
    public void reg(User user) {
        // To prevent unchecked parameters, first check parameters for compliance from the DB
        //........
    }
}
```

Optimized version using DDD:

```java
class SafeUser {
    User user;
}

public class TestController {
    @Autowired
    TestService testService;

    public void reg(User user) {
        boolean success = check(user);
        if (success) {
            SafeUser safeUser = new SafeUser(user);
            testService.reg(safeUser);
        }
    }
}

public class TestService {
    @Autowired
    HHHHService hhhhService;

    public void reg(SafeUser safeUser) {
        User user = safeUser.user;
        hhhhService.reg(user);
        //........
    }
}

public class HHHHService {
    public void reg(SafeUser safeUser) {
        User user = safeUser.user;
        //........
    }
}
```

With the Purah approach, when thread-local context caching is enabled, the user check will only be called once:

```java
@SpringBootApplication
@EnablePurah(enableCache = true) // Globally enable, default is false
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}

// Or
@MethodCheckConfig(enableCache = true)
public void reg(@CheckIt("User Check") User user) {
    testService.reg(user);
}
```

In a global enablement scenario, you can write it this way, but it's not recommended. `@CheckIt`​ is based on aspects and does not take effect when called in beans, making it easy to introduce bugs:

```java
public class TestController {
    @Autowired
    TestService testService;

    public void reg(@CheckIt("User Check") User user) {
        testService.reg(user);
    }
}

public class TestService {
    @Autowired
    HHHHService hhhhService;

    public void reg(@CheckIt("User Check") User user) {
        hhhhService.reg(user);
    }
}

public class HHHHService {
    public void reg(@CheckIt("User Check") User user) {
        // ...
    }
}

public class ShowService {
    public void reg(@CheckIt("User Check") User user) {
        testController.reg(user);
        testService.reg(user);
        hhhhService.reg(user);
    }
}

public static void main(String[] args) {
    // Total checks 4 times
    testController.reg(user); // Checked only once
    testService.reg(user);    // Checked only once
    hhhhService.reg(user);    // Checked only once
    showService.reg(user);     // Checked only once
}
```

// todo To be completed

```java
public class ValidArg<T> {
    T value;
    boolean valid = false;
}
```

### 10 @CheckIt Aspect Thrown Exception and @FillToMethodResult Filled Data

Whether it is an exception thrown by the aspect or data filled by @FillToMethodResult

They are essentially encapsulations of MethodHandlerCheckResult

```java
public class MethodArgCheckException extends BasePurahException {

    final MethodHandlerCheckResult checkResult;

    public MethodArgCheckException(MethodHandlerCheckResult checkResult) {
        super(checkResult.execInfo().name());
        this.checkResult = checkResult;

    }
}
public void checkThreeUserThrow(@CheckIt({"test", "test2"}) User user0,
                                @CheckIt("test") User user1,
                                @CheckIt("test") User user2) {
}

public void main() {
    MethodHandlerCheckResult stores the validation result of each parameter as ArgCheckResult, obtained via argResultOf(int index)
    ArgCheckResult stores the result of each rule

    methodHandlerCheckResult.argResultOf(0).resultOf("test")
    methodHandlerCheckResult.argResultOf(0).resultOf("test2")
    methodHandlerCheckResult.argResultOf(1).resultOf("test")
    // Enter ResultLevel to get all failed direct logic validation results
    List<LogicCheckResult<?>> failedList = methodHandlerCheckResult.childList(ResultLevel.only_failed_only_base_logic);

}
// ResultLevel levels
public enum ResultLevel {
    // All results, whether successful or not, regardless of whether they are direct validation logic results
    all(1),
    // All results, whether successful or not, only direct validation logic results
    all_only_base_logic(2),
    // Only failed results
    only_failed(3),
    // Only failed results, only direct validation logic results
    only_failed_only_base_logic(4);
}
```

@FillToMethodResult fills MethodHandlerCheckResult, and for boolean, it is methodHandlerCheckResult.isSuccess();
