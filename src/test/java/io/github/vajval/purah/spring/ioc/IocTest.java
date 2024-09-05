package io.github.vajval.purah.spring.ioc;

import com.google.common.collect.Sets;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.spring.ioc.test_bean.PurahConfigPropertiesBean;
import io.github.vajval.purah.spring.ioc.test_bean.TestCallBack;
import io.github.vajval.purah.spring.ioc.test_bean.checker.IocIgnoreChecker;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.IocIgnoreMatcher;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcherFactory;
import io.github.vajval.purah.util.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.ExampleApplication;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.spring.ioc.test_bean.checker.IocMethodRegTestBean;
import io.github.vajval.purah.spring.ioc.test_bean.checker.IocTestChecker;
import io.github.vajval.purah.spring.ioc.test_bean.matcher.ReverseStringMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

import java.util.Set;

@SpringBootTest(classes = ExampleApplication.class)
public class IocTest {
    @Autowired
    Purahs purahs;



    @Test
    public void ioc() {
        Assertions.assertTrue(containsChecker(IocTestChecker.NAME));
        Assertions.assertTrue(containsChecker(IocMethodRegTestBean.NOT_NULL_CHECKER_NAME));
        Assertions.assertTrue(containsChecker(IocMethodRegTestBean.RANGE_TEST));
        Assertions.assertTrue(purahs.checkerOf(IocMethodRegTestBean.RANGE_TEST).oCheck(2));
        Assertions.assertFalse(purahs.checkerOf(IocMethodRegTestBean.RANGE_TEST).oCheck(5));

        Assertions.assertFalse(containsChecker(IocIgnoreChecker.NAME));


        Assertions.assertTrue(containsMatcherFactory(ReverseStringMatcher.NAME));
        Assertions.assertTrue(containsMatcherFactory(ReverseStringMatcherFactory.NAME));
        Assertions.assertFalse(containsMatcherFactory(IocIgnoreMatcher.NAME));
//        Assertions.assertEquals(TestCallBack.value, 1);
        Assertions.assertDoesNotThrow(() -> purahs.checkerOf("user_reg"));
//        CheckResult<Object> result = purahs.checkerOf("用户注册检查").oCheck(new User(null, null, null, null));
//        result = purahs.checkerOf("example:1[][name:中文名字检测;*n*:中文名字检测]").oCheck(new User(null, null, null, null));
    }

    @Test
    public void toChecker() {

        Assertions.assertFalse(purahs.checkerOf("auto_null_failed").check(null));
        Assertions.assertTrue(purahs.checkerOf("auto_null_success").check(null));

        Assertions.assertTrue(purahs.checkerOf("auto_null_ignore").check(null).isIgnore());

        Assertions.assertThrows(Exception.class, () -> purahs.checkerOf("auto_null_ignore").check(null).isSuccess());
        Assertions.assertThrows(Exception.class, () -> purahs.checkerOf("auto_null_ignore").check(null).isFailed());
        Assertions.assertFalse(purahs.checkerOf("auto_null_notEnable").check(null));
        Assertions.assertTrue(purahs.checkerOf("auto_null_notEnable").oCheck("123"));
        Assertions.assertTrue(purahs.checkerOf("auto_null_ignore_combo").check(null));
        Assertions.assertFalse(purahs.checkerOf("auto_null_ignore_combo_failed").check(null));


    }

    @Test
    public void test() {
        FieldMatcher fieldMatcher = purahs.matcherOf(ReverseStringMatcher.NAME).create("abc");
        Set<String> strings = fieldMatcher.matchFields(Sets.newHashSet("123", "321", "abc", "cba"), null);
        Assertions.assertEquals(strings, Sets.newHashSet("cba"));

        fieldMatcher = purahs.matcherOf(ReverseStringMatcher.NAME).create("123");
        strings = fieldMatcher.matchFields(Sets.newHashSet("123", "321"), null);
        Assertions.assertEquals(strings, Sets.newHashSet("321"));
    }


    public boolean containsMatcherFactory(String factory) {

        try {
            return purahs.matcherOf(factory) != null;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean containsChecker(String name) {
        try {
            return purahs.checkerOf(name) != null;
        } catch (Exception e) {
            return false;
        }

    }
//
//    @Test
//    public void iosc() throws NoSuchMethodException {
//        CheckBean checkBean = new CheckBean();
//        LambdaChecker<String> checker = LambdaChecker.of(String.class)
//                .build("lambdaTest", checkBean::lambdaTest);
//        Checker<Object, Object> objectObjectChecker = purahs.checkerOf("lambdaTest");
//
//        Method method = CheckBean.class.getMethod("lambdaTest", String.class);
//
//
//        ByLogicMethodChecker byLogicMethodChecker = new ByLogicMethodChecker(checkBean, method, "lambdaTest", AutoNull.notEnable);
//        GenericsProxyChecker genericsProxyChecker = (GenericsProxyChecker) objectObjectChecker;
//        System.out.println(genericsProxyChecker.defaultChecker.getClass());
//        GenericsProxyChecker byChecker = GenericsProxyChecker.createByChecker(checker);
//        MyCustomAnnChecker myCustomAnnChecker=new MyCustomAnnChecker();
//        LambdaChecker<Integer> notNull = LambdaChecker.of(Integer.class).annBuild(NotNull.class.toString(), NotNull.class, myCustomAnnChecker::notNull);
//        for (int i = 0; i < 100000000; i++) {
//            notNull.oCheck(111111111);
////            checker.check(InputToCheckerArg.of("111111111"));
////
//        }
//
//
//    }

}
