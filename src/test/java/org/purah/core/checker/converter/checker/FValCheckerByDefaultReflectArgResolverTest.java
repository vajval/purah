package org.purah.core.checker.converter.checker;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.purah.core.name.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.util.TestUser;
import org.purah.util.People;
import org.purah.util.TestAnn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FValCheckerByDefaultReflectArgResolverTest {
//    child#0, child#0.child, name, child#0.child#0.child

    public static boolean childNameCheck(@FVal("$root$") InputToCheckerArg<People> peopleArg,
                                         @FVal("name") String name,
                                         @FVal("name") TestAnn testAnnOnNameField,
                                         @FVal("name") Name noExistAnn,
//                                         @FVal("child#*.child#*") Map<String, People> childChildMap,
                                         @FVal("child") List<People> childList,
                                         @FVal("child#0") People child0,
                                         @FVal("child#100") People child100,
                                         @FVal("child#0.child") List<People> child0ChildList,
                                         @FVal("child#0.child#0.name") String childChildName,
                                         @FVal("child#0.child#0.child") List<People> superChildList) {
        People root = People.elder;
        People people = peopleArg.argValue();
        if (!root.equals(people)) {
            return false;
        }
        if (noExistAnn != null) {
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
        return superChildList == null;

    }

    @Test
    void check() {

        Method method = Stream.of(FValCheckerByDefaultReflectArgResolverTest.class.getDeclaredMethods()).filter(i -> i.getName().equals("childNameCheck")).collect(Collectors.toList()).get(0);
        FValCheckerByDefaultReflectArgResolver checker = new FValCheckerByDefaultReflectArgResolver(null, method, "test");

        for (int i = 0; i < 3; i++) {
            Assertions.assertTrue(checker.check(People.elder));
        }

    }


    public static boolean childNameCheck2(
            @FVal("id") Long w,
            @FVal("name") Name e,
            @FVal("a.name") String r,
            @FVal("child.id") Long testAnnOnNameField,
            @FVal("child.name") Name noExistAnn,
            @FVal("child.address") String address,
            @FVal("child") TestUser testUser2,
            @FVal("child.child.id") Long testAnnOnNameField2,
            @FVal("child.child.name") Name noExistAnn2,
            @FVal("child.child.address") String address2,
            @FVal("child.child") TestUser testUse2r


    ) {
        return address2 != null;

    }



    @Test
    void check2() {
        Method method = Stream.of(FValCheckerByDefaultReflectArgResolverTest.class.getDeclaredMethods()).filter(i -> i.getName().equals("childNameCheck2")).collect(Collectors.toList()).get(0);

        FValCheckerByDefaultReflectArgResolver checker = new
                FValCheckerByDefaultReflectArgResolver(
                null, method, "test");

        TestUser testUser = new TestUser(1L, "name", "address");
        testUser.child = new TestUser(2L, "child_name", "child_address");
        testUser.child.child = new TestUser(4L, "child_child_name", "child_child_address");


        for (int i = 0; i < 300; i++) {
            Assertions.assertTrue(checker.check(testUser));
        }
    }

    @Test
    void check3() {
        Method method = Stream.of(FValCheckerByDefaultReflectArgResolverTest.class.getDeclaredMethods()).filter(i -> i.getName().equals("childNameCheck")).collect(Collectors.toList()).get(0);

        FValCheckerByDefaultReflectArgResolver checker = new
                FValCheckerByDefaultReflectArgResolver(
                null, method, "test");

        for (int i = 0; i < 10000; i++) {
            Assertions.assertTrue(checker.check(People.elder).isSuccess());
        }
    }

    public static class Test2 {

        final Map<String, String> map = Collections.singletonMap("1", "2");

        public Map<String, String> getMap() {
            return map;
        }

        public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            Test2 test = new Test2();
            System.out.println(PropertyUtils.getProperty(test, "map.1"));
        }
    }
}