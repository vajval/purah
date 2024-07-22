package org.purah.core.matcher.nested;

import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.resolver.ReflectArgResolver;
import org.purah.util.People;
import org.purah.util.TestAnn;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;


class AnnByPackageMatcherTest {


    @Test
    void annByPackageMatcher() {
        //todo    list nested
        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("org*") {
            @Override
            protected boolean needBeCollected(Field field) {
                TestAnn testAnn = field.getDeclaredAnnotation(TestAnn.class);
                return testAnn != null && StringUtils.hasText(testAnn.value());
            }


        };

        for (int i = 0; i < 1; i++) {

            ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(People.son, annByPackageMatcher);
            System.out.println(matchFieldObjectMap.keySet());
        }
    }


}