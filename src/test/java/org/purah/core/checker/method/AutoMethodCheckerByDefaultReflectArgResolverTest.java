package org.purah.core.checker.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.converter.checker.FValCheckerByDefaultReflectArgResolver;
import org.purah.core.checker.converter.checker.FVal;
import org.purah.util.People;
import org.purah.core.base.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.util.TestAnn;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class AutoMethodCheckerByDefaultReflectArgResolverTest {










//    child#0, child#0.child, name, child#0.child#0.child

    public static boolean childNameCheck(@FVal("#root#") InputToCheckerArg<People> peopleArg,
                                         @FVal("name") String name,
                                         @FVal("name") TestAnn testAnnOnNameField,
                                         @FVal("name") Name noExistAnn,
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
        if (noExistAnn!=null) {
            return false;
        }

        if (!Objects.equals(testAnnOnNameField.value(), "不超过3个字")) {
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

        Method method = AutoMethodCheckerByDefaultReflectArgResolverTest.class.getDeclaredMethod(
                "childNameCheck", InputToCheckerArg.class,
                String.class, TestAnn.class,Name.class, List.class,
                People.class, People.class, List.class,
                String.class,
                List.class);

        FValCheckerByDefaultReflectArgResolver checker = new
                FValCheckerByDefaultReflectArgResolver(
                        null, method, "test");
        Assertions.assertTrue(checker.check(People.of("长者")).isSuccess());
    }

    @Test
    void doCheck() {
    }
}