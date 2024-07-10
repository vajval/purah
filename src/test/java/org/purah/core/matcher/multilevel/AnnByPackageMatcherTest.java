package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.resolver.reflect.ReflectArgResolver;
import org.purah.util.People;
import org.purah.util.TestAnn;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;


class AnnByPackageMatcherTest {


    @Test
    void annByPackageMatcher() {

        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("org*") {
            @Override
            protected boolean needBeChecked(Field field) {
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