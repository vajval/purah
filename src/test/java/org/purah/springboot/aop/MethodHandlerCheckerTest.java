package org.purah.springboot.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.core.PurahContext;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.checker.result.LogicCheckResult;
import org.purah.springboot.aop.ann.CheckIt;
import org.purah.springboot.aop.ann.FillToMethodResult;
import org.purah.springboot.aop.ann.MethodCheckConfig;
import org.purah.springboot.aop.result.ArgCheckResult;
import org.purah.springboot.aop.result.MethodHandlerCheckResult;
import org.purah.springboot.ioc.PurahIocRegS;
import org.purah.springboot.ioc.ann.ToChecker;

import org.purah.util.People;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MethodHandlerCheckerTest {

    private static final Logger log = LogManager.getLogger(MethodHandlerCheckerTest.class);
    PurahContext purahContext;


    @ToChecker("not null")
    public static boolean notNull(People parent) {
        return parent != null;
    }

    @ToChecker("have_child")
    public static CheckResult<String> haveChild(People people) {

        if (CollectionUtils.isEmpty(people.getChild())) {
            return LogicCheckResult.failed("no child");
        }

        return LogicCheckResult.success("success");
    }


    public void test(@CheckIt("not null") People parent) {

    }

    @FillToMethodResult
    public CheckResult<String> test2(@CheckIt("have_child") People parent) {
        return null;
    }

    @FillToMethodResult
    public CheckResult<String> test3(@CheckIt("have_child") People parent0, @CheckIt("have_child") People parent1, @CheckIt People parent2, People parent3, @CheckIt("have_child") People parent4, People parent5, @CheckIt People parent6, @CheckIt("have_child") People parent7) {
        return null;
    }

    @FillToMethodResult
    @MethodCheckConfig(mainMode = ExecMode.Main.all_success_but_must_check_all)
    public CheckResult<String> test4(@CheckIt("have_child") People parent0, @CheckIt("have_child") People parent1, @CheckIt People parent2, People parent3, @CheckIt("have_child") People parent4, People parent5, @CheckIt People parent6, @CheckIt("have_child") People parent7) {
        return null;
    }

    MethodHandlerCheckerTest bean;

    @BeforeEach
    public void beforeEach() {
        purahContext = new PurahContext();
        bean = new MethodHandlerCheckerTest();
        PurahIocRegS purahIocRegS = new PurahIocRegS(purahContext);
        purahIocRegS.regPurahMethodsRegBean(bean);

    }

    protected MethodHandlerChecker checkerByMethodName(String methodName) {
        Method method = Stream.of(MethodHandlerCheckerTest.class.getMethods()).filter(i -> i.getName().equals(methodName)).collect(Collectors.toList()).get(0);
        return new MethodHandlerChecker(bean, method, purahContext.purahs());
    }

    @Test
    public void method1() {

        MethodHandlerChecker methodHandlerChecker = checkerByMethodName("test");


        Assertions.assertTrue(methodHandlerChecker.check(new People[]{People.elder}));
        Assertions.assertTrue(methodHandlerChecker.check(new People[]{People.daughter}));
        Assertions.assertFalse(methodHandlerChecker.check(new People[]{null}));


    }

    @Test
    public void method2() {


        MethodHandlerChecker methodHandlerChecker = checkerByMethodName("test2");
        MethodHandlerCheckResult methodHandlerCheckResult = methodHandlerChecker.check(new People[]{People.elder});
        Object haveChild = methodHandlerCheckResult.argResultOf(0).resultOf("have_child").data();
        Assertions.assertEquals(haveChild, "success");

        methodHandlerCheckResult = methodHandlerChecker.check(new People[]{People.granddaughterForDaughter});
        haveChild = methodHandlerCheckResult.argResultOf(0).resultOf("have_child").data();
        Assertions.assertEquals(haveChild, "no child");
        log.info("----------------");
        log.info(methodHandlerCheckResult.log());
        log.info(methodHandlerCheckResult.argResultOf(0).log());
        log.info(methodHandlerCheckResult.argResultOf(0).resultOf("have_child").data());


    }

    @Test
    public void method3() {
        MethodHandlerChecker methodHandlerChecker = checkerByMethodName("test3");
        MethodHandlerCheckResult methodHandlerCheckResult = methodHandlerChecker.check(new People[]{People.elder, People.granddaughterForDaughter, People.elder, People.elder, People.elder, People.elder, People.elder, People.elder});
        List<ArgCheckResult> resultList = methodHandlerCheckResult.data();
        Assertions.assertTrue(resultList.get(0));
        Assertions.assertFalse(resultList.get(1));
        for (int index = 2; index < resultList.size(); index++) {
            log.info(methodHandlerCheckResult.argResultOf(index));
            Assertions.assertTrue(methodHandlerCheckResult.argResultOf(index).isIgnore());
        }
    }
    @Test
    public void method4() {
        MethodHandlerChecker methodHandlerChecker = checkerByMethodName("test4");
        MethodHandlerCheckResult methodHandlerCheckResult = methodHandlerChecker.check(new People[]{People.elder, People.granddaughterForDaughter, People.elder, People.elder, People.elder, People.elder, People.elder, People.elder});
        List<ArgCheckResult> resultList = methodHandlerCheckResult.data();
        Assertions.assertTrue(resultList.get(0));
        Assertions.assertFalse(resultList.get(1));
        for (int index =2; index < resultList.size(); index++) {
            ParameterHandlerChecker parameterHandlerChecker = methodHandlerChecker.parameterHandlerCheckerMap.get(index);
            log.info(methodHandlerCheckResult.argResultOf(index));
            if(parameterHandlerChecker==null){
                Assertions.assertTrue(methodHandlerCheckResult.argResultOf(index).isIgnore());
            }else{
                Assertions.assertTrue(methodHandlerCheckResult.argResultOf(index));
            }
        }

    }
}