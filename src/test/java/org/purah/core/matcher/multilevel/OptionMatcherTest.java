package org.purah.core.matcher.multilevel;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Test;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.resolver.ReflectArgResolver;
import org.purah.util.People;
import org.purah.util.TestAnn;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OptionMatcherTest {

    public static void main(String[] args) {
//
//        String str = "child#0.child#4";
//
//        System.out.println(stringBuilder.toString());

    }

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {


        //
        OptionMatcher optionMatcher = new OptionMatcher("child#0.name");
        ReflectArgResolver reflectArgResolver = new ReflectArgResolver();
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = reflectArgResolver.getMatchFieldObjectMap(People.of("孙子"), optionMatcher);
//

        System.out.println(matchFieldObjectMap);
    }

}