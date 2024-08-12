package io.github.vajval.purah.core.matcher.nested;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.util.People;
import io.github.vajval.purah.util.TestAnn;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;


public class AnnByPackageMatcherTest {

    public static class A {
        @TestAnn
        String name;
        B b;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    public static class B {
        @TestAnn
        String id;
        A a;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }

    @Test
    void an234n() {
        A a = new A();
        a.name = "name";
        B b = new B();
        a.b = b;
        b.a = a;
        b.id = "id";
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("*",2) {
            @Override
            protected boolean needBeCollected(Field field) {
                TestAnn testAnn = field.getDeclaredAnnotation(TestAnn.class);
                return testAnn != null ;
            }
        };
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.oGetMatchFieldObjectMap(a, annByPackageMatcher);
        Assertions.assertEquals(matchFieldObjectMap.keySet(),Sets.newHashSet("b.a.name", "name", "b.id"));
    }

    @Test
    void ann() {
        A a = new A();
        a.name = "name";
        B b = new B();
        a.b = b;
        b.a = a;
        b.id = "id";
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        Set<Class<? > >annClazz=    Sets.newHashSet(TestAnn.class);

        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("*",2,TestAnn.class) ;
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.oGetMatchFieldObjectMap(a, annByPackageMatcher);
        Assertions.assertEquals(matchFieldObjectMap.keySet(),Sets.newHashSet("b.a.name", "name", "b.id"));
    }



    @Test
    void annByPackageMatcher() {
        //todo    list nested
        AnnByPackageMatcher annByPackageMatcher = new AnnByPackageMatcher("*vajval*") {
            @Override
            protected boolean needBeCollected(Field field) {
                TestAnn testAnn = field.getDeclaredAnnotation(TestAnn.class);
                return testAnn != null && StringUtils.hasText(testAnn.value());
            }


        };

        for (int i = 0; i < 1; i++) {
            ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
            Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.oGetMatchFieldObjectMap(People.son, annByPackageMatcher);
            System.out.println(matchFieldObjectMap.keySet());
        }
    }


}