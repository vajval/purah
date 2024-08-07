package io.github.vajval.purah.core.checker.converter.checker;

import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import io.github.vajval.purah.util.People;
import io.github.vajval.purah.util.TestAnn;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.util.TestUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FValCheckerByDefaultReflectArgResolverTest {


    public static boolean childNameCheck(@FVal(FieldMatcher.rootField) InputToCheckerArg<People> peopleArg,
                                         @FVal("name") String name,
                                         @FVal("name") TestAnn testAnnOnNameField,
                                         @FVal("name") Name noExistAnn,
                                         @FVal("child") List<People> childList,
                                         @FVal("child#0") People child0,
                                         @FVal("child#100") People child100,
                                         @FVal("child#0.child") List<People> child0ChildList,
                                         @FVal("child#0.child#0.name") String childChildName,
                                         @FVal("child#0.child#0.child") List<People> superChildList) {
        People root = People.elder;
        People beCheckPeople = peopleArg.argValue();
        if (!root.equals(beCheckPeople)) {
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
        FValMethodChecker checker = new FValMethodChecker(null, method, "test",AutoNull.notEnable);

        FixedMatcher fixedMatcher = new FixedMatcher("child#0.child#0.name|child#0|child#0.child|child#100|name|child#0.child#0.child|child");
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        for (int i = 0; i < 3; i++) {
            Assertions.assertTrue(checker.oCheck(People.elder));
        }

    }

    static TestUser testUser;

    @BeforeEach
    public void a() {
        testUser = new TestUser(1L, "name", "address");
        testUser.child = new TestUser(2L, "child_name", "child_address");
        testUser.child.child = new TestUser(4L, "child_child_name", "child_child_address");
    }


    public static boolean childNameCheck2(
            @FVal("id") Long id,
            @FVal("name") TestAnn name,
            @FVal("a.name") String aName,
            @FVal("child.id") Long childId,
            @FVal("child.child.id") Long childChildId,

            @FVal("child.name") TestAnn childName,
            @FVal("child.address") String childAddress,
            @FVal("child") TestUser child,
            @FVal("child.child.name") Name childChildName,
            @FVal("child.child.address") String childChildAddress,
            @FVal("child.child") TestUser childChild

    ) {
        if (id != null) {
            return false;
        }
        if (aName != null) {
            return false;
        }
        if (!name.value().equals("name")) {
            return false;
        }
        if (childId != null) {
            return false;
        }
        if (childChildId != null) {
            return false;
        }
        if (!childName.value().equals("name")) {
            return false;
        }
        if (childChildName != null){
            return false;
        }
        if (!childAddress.equals(testUser.getChild().address)) {
            return false;
        }
        if (!child.equals(testUser.getChild())) {
            return false;
        }
        if (!childChildAddress.equals(testUser.getChild().getChild().getAddress())) {
            return false;
        }
        if (!childChild.equals(testUser.getChild().getChild())) {
            return false;
        }
        return true;
    }


    @Test
    void check2() {
        Method method = Stream.of(FValCheckerByDefaultReflectArgResolverTest.class.getDeclaredMethods()).filter(i -> i.getName().equals("childNameCheck2")).collect(Collectors.toList()).get(0);

        FValMethodChecker checker = new
                FValMethodChecker(
                null, method, "test",AutoNull.notEnable);


        for (int i = 0; i < 3*1000; i++) {//no cache 3*1000*1000=13s cache 9.5s
            Assertions.assertTrue(checker.oCheck(testUser));
        }
    }

    @Test
    void check3() {
        Method method = Stream.of(FValCheckerByDefaultReflectArgResolverTest.class.getDeclaredMethods()).filter(i -> i.getName().equals("childNameCheck")).collect(Collectors.toList()).get(0);

        FValMethodChecker checker = new
                FValMethodChecker(
                null, method, "test",AutoNull.notEnable);

        for (int i = 0; i < 10000; i++) {
            Assertions.assertTrue(checker.oCheck(People.elder).isSuccess());
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