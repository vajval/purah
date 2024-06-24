package org.purah.core.checker.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.People;
import org.purah.core.checker.InputToCheckerArg;

import java.lang.reflect.Method;
import java.util.List;

public class AutoMethodCheckerByDefaultReflectArgResolverTest {


    public static boolean childNameCheck(@FVal("#root#") InputToCheckerArg<People> peopleArg,
                                         @FVal("name") String name,
                                         @FVal("child") List<People> childList,
                                         @FVal("child#0") People child0,
                                         @FVal("child#100") People child100,
                                         @FVal("child#0.child") List<People> child0ChildList,
                                         @FVal("child#0.child#0.name") String childChildName,
                                         @FVal("child#0.child#0.child") List<People> superChildList) {
        People root = People.of("长者");
        People people = peopleArg.argValue();
        if (!root.equals(people)) {
            return false;
        }
        if (!root.getName().equals(name)) {
            return false;
        }
        if (!root.getChild().equals(childList)) {
            return false;
        }
        if (!root.getChild().get(0).equals(child0)) {
            return false;
        }
        if (child100 != null) {
            return false;
        }
        if (!root.getChild().get(0).getChild().equals(child0ChildList)) {
            return false;
        }
        if (!root.getChild().get(0).getChild().get(0).getName().equals(childChildName)) {
            return false;
        }
        if (superChildList != null) {
            return false;
        }

        return true;

    }


    @Test
    void purahEnableMethod() throws NoSuchMethodException {
        for (Method method : AutoMethodCheckerByDefaultReflectArgResolverTest.class.getMethods()) {
            System.out.println(method);
        }
        Method method = AutoMethodCheckerByDefaultReflectArgResolverTest.class.getDeclaredMethod(

                "childNameCheck", InputToCheckerArg.class,
                String.class, List.class,
                People.class, People.class, List.class,
                String.class,
                List.class);

        AutoMethodCheckerByDefaultReflectArgResolver checker = new AutoMethodCheckerByDefaultReflectArgResolver(null, method, "test"
        );
        Assertions.assertTrue(checker.check(People.of("长者")).isSuccess());
    }

    @Test
    void doCheck() {
    }
}