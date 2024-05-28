package org.purah.core.checker;


import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckerException;
import org.purah.core.exception.PurahException;
import org.springframework.core.ResolvableType;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExecChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    String name;
    CheckClass BaseLogicCheckClass;
    Checker<?, ?> BaseLogicChecker;


    Map<CheckClass, Checker<?, ?>> checkerMap = new ConcurrentHashMap<>();


    public ExecChecker(String name) {
        this.name = name;

    }


    public void addNewChecker(Checker<?, ?> checker) {
        if (BaseLogicChecker == null) {
            this.BaseLogicChecker = checker;
            this.BaseLogicCheckClass = CheckClass.byChecker(checker);
        }
        CheckClass checkClass = CheckClass.byChecker(checker);
        this.checkerMap.put(checkClass, checker);
        if (checkClass.equals(BaseLogicCheckClass)) {
            BaseLogicChecker = checker;
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult check(CheckInstance<CHECK_INSTANCE> checkInstance) {


        CheckClass checkClass = CheckClass.byInstance(checkInstance.instance());
        Checker<?, ?> checker = getChecker(checkClass);

        if (checker == null) {
            throw new CheckerException(this, "checker " + this.name + "没有对该类的解析方法" + checkClass.clazz);
        }
        CheckResult<?> checkResult;
        try {

            checkResult = ((Checker) checker).check(checkInstance);


        } catch (PurahException exception) {
            throw exception;
        }
        return checkResult;
    }

    protected Checker<?, ?> getChecker(CheckClass inputCheckClass) {

        if (inputCheckClass.clazz == null) {

//            if (checkerMap.size() != 1) {
//                throw new RuntimeException("入参为null，而尔");
//            }
            return BaseLogicChecker;
        }

        if (BaseLogicSupport(inputCheckClass)) return BaseLogicChecker;
        Checker<?, ?> result = checkerMap.get(inputCheckClass);
        if (result != null) return result;
        for (Map.Entry<CheckClass, Checker<?, ?>> entry : checkerMap.entrySet()) {
            CheckClass entryKey = entry.getKey();
            if (support(inputCheckClass, entryKey)) {
                result = entry.getValue();
                checkerMap.put(inputCheckClass, result);
                return result;
            }
        }
        return null;
    }

    protected boolean BaseLogicSupport(CheckClass checkClass) {
        return BaseLogicCheckClass.support(checkClass);
    }


    protected boolean support(CheckClass inputCheckClass, CheckClass checkClass) {
        if (inputCheckClass.clazz == checkClass.clazz) return true;
        return checkClass.support(inputCheckClass);
    }

    static class CheckClass {

        final static CheckClass empty = new CheckClass();

        Class<?> clazz;


        public boolean support(CheckClass inputCheckClass) {
            if (this == inputCheckClass) return true;
            if (this.clazz == inputCheckClass.clazz) return true;
            if (inputCheckClass.clazz == null) {
                return false;
            }
            /*
             * 能处理Map 就一定能处理 HashMap
             */
            return this.clazz.isAssignableFrom(inputCheckClass.clazz);
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            return Objects.equals(clazz, ((CheckClass) obj).clazz);

        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz);
        }

        static CheckClass byInstance(Object instance) {
            if (instance != null) {
                return byClass(instance.getClass());
            }
            return empty;
        }

        static CheckClass byClass(Class<?> clazz) {
            CheckClass checkClass = new CheckClass();
            if (clazz == null) clazz = Object.class;
            checkClass.clazz = clazz;
            return checkClass;
        }

        static CheckClass byChecker(Checker<?, ?> checker) {
            if (checker instanceof BaseChecker ) {

                BaseChecker baseChecker=(BaseChecker) checker;
                return byClass(baseChecker.inputCheckInstanceClass());
            }
            ResolvableType[] generics =
                    ResolvableType
                            .forClass(checker.getClass())
                            .as(Checker.class)
                            .getGenerics();
            return byClass(generics[0].resolve());
        }

        @Override
        public String toString() {
            return "CheckClass{" +
                    "clazz=" + clazz +
                    '}';
        }
    }


}