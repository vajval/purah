package org.purah.core.matcher.multilevel;

import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.extra.AnnByPackageMatcher;
import org.purah.core.resolver.ReflectArgResolver;
import org.purah.util.TestAnn;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;


class AnnByPackageMatcherTest {


    @Test
    void annByPackageMatcher() {

        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("org*") {
            @Override
            protected boolean fieldCheck(Field field) {
                TestAnn testAnn = field.getDeclaredAnnotation(TestAnn.class);
                return testAnn != null && StringUtils.hasText(testAnn.value());
            }
        };

        AnnTest annTest = new AnnTest("长者", 1);

        for (int i = 0; i < 10; i++) {

            ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(annTest, annByPackageMatcher);
            System.out.println(matchFieldObjectMap.keySet());
        }


    }


    public static class AnnTest {
        @TestAnn("name")
        String name;
        Integer value;

        ChildAnnTest child = new ChildAnnTest("儿子", 2, new ChildAnnTest("孙子", 3));

        public AnnTest(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public ChildAnnTest getChild() {
            return child;
        }

        public void setChild(ChildAnnTest child) {
            this.child = child;
        }

        @Override
        public String toString() {
            return "AnnTest{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", child=" + child +
                    '}';
        }
    }

    public static class ChildAnnTest {
        @TestAnn("name")
        String name;

        Integer value;
        ChildAnnTest child;

        public ChildAnnTest(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public ChildAnnTest(String name, Integer value, ChildAnnTest child) {
            this.name = name;
            this.value = value;
            this.child = child;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public ChildAnnTest getChild() {
            return child;
        }

        public void setChild(ChildAnnTest child) {
            this.child = child;
        }

        @Override
        public String toString() {
            return "ChildAnnTest{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", child=" + child +
                    '}';
        }
    }
}