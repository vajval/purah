//package org.purah.util;
//
//import com.google.common.collect.Sets;
//import org.purah.core.Purahs;
//import org.purah.core.checker.Checker;
//import org.purah.core.checker.CustomAnnChecker;
//import org.purah.core.checker.InputToCheckerArg;
//import org.purah.core.checker.ann.CNPhoneNum;
//import org.purah.core.checker.ann.NotEmptyTest;
//import org.purah.core.checker.ann.NotNull;
//import org.purah.core.checker.ann.Range;
//import org.purah.core.checker.combinatorial.ExecMode;
//import org.purah.core.checker.result.CheckResult;
//import org.purah.core.checker.result.LogicCheckResult;
//import org.purah.core.checker.result.MultiCheckResult;
//import org.purah.core.checker.result.ResultLevel;
//import org.purah.core.matcher.nested.AnnByPackageMatcher;
//import org.purah.core.matcher.nested.GeneralFieldMatcher;
//import org.purah.core.name.Name;
//import org.purah.springboot.aop.ann.CheckIt;
//import org.purah.springboot.ioc.ann.ToChecker;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//
//@Name("自定义注解检测")
//@Component
//public class MyCustomAnnChecker extends CustomAnnChecker {
//    @Autowired
//    Purahs purahs;
//
//    public MyCustomAnnChecker(ExecMode.Main mainExecType, ResultLevel resultLevel) {
//        super(mainExecType, resultLevel);
//    }
//
//    public CheckResult<?> cnPhoneNum(CNPhoneNum cnPhoneNum, InputToCheckerArg<String> str) {
//        String strValue = str.argValue();
//        //gpt小姐 说的
//        if (strValue.matches("^1[3456789]\\d{9}$")) {
//            return LogicCheckResult.successBuildLog(str, "正确的");
//        }
//        return LogicCheckResult.failed(str.argValue(), str.fieldPath() + ":" + cnPhoneNum.errorMsg());
//    }
//
//    public boolean notNull(NotNull notNull, Integer age) {
//        return age != null;
//    }
//
//    public CheckResult<?> range(Range range, InputToCheckerArg<Number> num) {
//        Number numValue = num.argValue();
//        if (numValue.doubleValue() < range.min() || numValue.doubleValue() > range.max()) {
//            return LogicCheckResult.failed(num.argValue(), (num.fieldPath() + ":" + range.errorMsg()));
//        }
//        return LogicCheckResult.successBuildLog(num, "参数合规");
//    }
//}
//package org.Company;
//
//class People {
//    @CheckIt("电话检测")
//    String phone;
//    @CheckIt("姓名检测")
//    String name;
//}
//package org.Company;
//
//class User {
//    @CheckIt("id检测")
//    Long id;
//    @CheckIt("人员信息检测")
//    People people;
//
//    public void userReg(@CheckIt("example:1[用户注册检查][age:年龄合法检查]") User user) {
//        purahs.checkerOf("").check().isFailed()
//    }
//
//    @ToChecker("用户注册检查")
//    public Checker<?, ?> phoneAddress() {
//        return purahs.combo("手机号所属地址检测").match(new GeneralFieldMatcher("name"), "中文名字检测");
//    }
//
//    Purahs purahs;
//
//    //  或者 逻辑
//    public void reg(User user) {
//        public void userReg (
//                @CheckIt("example:1[][*:自定义注解检测]") User user){
//        }
//        //或者
//        public void reg (User user){
//            MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
//                    .match(new GeneralFieldMatcher("*"), "自定义注解检测")
//                    .check(user);
//        }
//        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("org.MyCompany.*|org.MyCompany2.*") {
//            @Override
//            protected boolean needBeCollected(Field field) {
//                Set<Class<? extends Annotation>> annotationSet = Sets.newHashSet(Range.class, CNPhoneNum.class, NotEmptyTest.class);
//                for (Class<? extends Annotation> aClass : annotationSet) {
//                    if (field.getDeclaredAnnotationsByType(aClass) != null) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        };
//        MultiCheckResult<CheckResult<?>> checkResult = purahs.combo()
//                .match(annByPackageMatcher, "自定义注解检测")
//                .check(user).;
//
//        purahs.combo("手机号所属地址检测")
//                .match(new GeneralFieldMatcher("name"),"中文名字检测")
//                .mainMode(ExecMode.Main.all_success);
//    }
//
//    class SafeUser {
//        User user ;
//    }
//
//    public class HHHHService {
//        public void reg(User user) {
//
//        }
//    }
//
//    public class TestService {
//        @Autowired
//        HHHHService hhhhService;
//
//        public void reg(User user) {
//            hhhhService.reg(user);
//        }
//
//    }
//    public class TestController {
//        @Autowired
//        TestService testService;
//
//        public void reg(User user) {
//            testService.reg(user);
//        }
//
//    }
//
//    public static void main(String[] args) {
//        testController.reg( user);
//        TestService.reg( user);
//        hhhhService.reg( user);
//    }
//            TestService.reg( user);
//    public class TestController {
//        @Autowired
//        TestService testService;
//
//        public void reg(User user) {
//            boolean success= check(user);
//            if(success){
//                SafeUser  safeUser  =new SafeUser(user)
//                testService.reg(safeUser);
//            }
//        }
//
//    }
//
//    public class TestService {
//        @Autowired
//        HHHHService hhhhService;
//        public void reg(SafeUser user) {
//            hhhhService.reg(user);
//        }
//    }
//
//    public class HHHHService {
//        public void reg(SafeUser user) {
//
//        }
//    }
//}
