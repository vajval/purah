package org.purah.springboot.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.purah.ExampleApplication;
import org.purah.core.checker.Checker;
import org.purah.core.checker.factory.CheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(classes = ExampleApplication.class)
class PurahIocSTest {

    @Autowired
    ApplicationContext applicationContext;

    PurahIocS purahIocS;

//    @BeforeEach
//    public void beanMethodToCheckerMap() {
//        purahIocS = new PurahIocS(applicationContext);
//    }
//
//    @Test
//    void inf() {
////        applicationContext.getBean(TestIntf.class);
//    }
//
//    @Test
//    void enableCheckers() {
//
//        Set<Checker> checkers = purahIocS.enableBeanSetByClass(Checker.class);
//        Optional<Checker> iocTestCheckerOptional = checkers.stream().filter(i -> i.name().equals("IocTestChecker")).findFirst();
//        Assertions.assertTrue(iocTestCheckerOptional.isPresent());
//        Checker checker = iocTestCheckerOptional.get();
//        Assertions.assertTrue(checker.check("IocTestChecker").isSuccess());
//        Assertions.assertFalse(checker.check("IocTestChecker2").isSuccess());
//
//    }
//
//    @Test
//    void purahEnableMethodsBean() {
//        Set<Object> purahEnableMethodsBeans = purahIocS.purahEnableMethodsBean();
//        Set<? extends Class<?>> clazzSet = purahEnableMethodsBeans.stream().map(Object::getClass).collect(Collectors.toSet());
//        Assertions.assertTrue(clazzSet.contains(IocTest.class));
//    }
//
//
//    @Test
//    void methodToChecker() {
//
//
//        Set<Object> purahEnableMethodsBeans = purahIocS.purahEnableMethodsBean();
//
//        List<Checker> checkers = purahIocS.checkersByBeanMethod();
//        Set<String> collect = checkers.stream().map(i -> i.name()).collect(Collectors.toSet());
//
//        for (String s : IocTest.checkerMethodNameList) {
//            Assertions.assertTrue(collect.contains(s));
//        }
//
//
//    }
//
//
//    @Test
//    void beanMethodToCheckerFactoryMap() {
//        List<CheckerFactory> checkerFactoryList = purahIocS.checkerFactoriesByBeanMethod();
//
//
//    }
//
//    @Test
//    void regChecker() {
//    }
}