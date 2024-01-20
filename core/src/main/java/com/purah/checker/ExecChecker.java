package com.purah.checker;

import com.purah.base.NameUtil;
import com.purah.checker.context.CheckerResult;
import com.purah.exception.BaseException;
import org.springframework.core.ResolvableType;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExecChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    String name;
    CheckClass singleCheckClass;
    Checker<?, ?> singleChecker;


    Map<CheckClass, Checker<?, ?>> checkerMap = new ConcurrentHashMap<>();


    public ExecChecker(String name) {
        this.name=name;

    }


    public void addNewChecker(Checker<?, ?> checker) {
        if(singleChecker==null){
            this.singleChecker = checker;
            this.singleCheckClass = CheckClass.byChecker(checker);
        }
        CheckClass checkClass = CheckClass.byChecker(checker);
        this.checkerMap.put(checkClass, checker);
    }


    @Override
    public CheckerResult check(CheckInstance<CHECK_INSTANCE> checkInstance) {



        CheckClass checkClass = CheckClass.byInstance(checkInstance.instance());
        Checker<?, ?> checker = getChecker(checkClass);
        if (checker == null) {
            throw new RuntimeException(this.name + "没有对该类的解析方法" + checkClass);
        }
        CheckerResult<?> checkerResult;
        try {

            checkerResult = ((Checker) checker).check( checkInstance);


        } catch (BaseException exception) {
            throw new RuntimeException();
        }
        return checkerResult;
    }

    protected Checker<?, ?> getChecker(CheckClass inputCheckClass) {
        if (singleSupport(inputCheckClass)) return singleChecker;
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

    protected boolean singleSupport(CheckClass checkClass) {
        return singleCheckClass.support(checkClass);
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
            return inputCheckClass.clazz.isAssignableFrom(inputCheckClass.clazz);
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            return Objects.equals(clazz, ((CheckClass) obj).clazz);

        }

        static CheckClass byInstance(Object instance) {
            if (instance != null) {
                return byClass(instance.getClass());
            }
            return empty;
        }

        static CheckClass byClass(Class<?> clazz) {
            CheckClass checkClass = new CheckClass();
            checkClass.clazz = clazz;
            return checkClass;
        }

        static CheckClass byChecker(Checker<?, ?> checker) {
            if (checker instanceof BaseChecker baseChecker) {
                return byClass(baseChecker.inputCheckInstanceClass());
            }
            ResolvableType[] generics =
                    ResolvableType
                            .forClass(checker.getClass())
                            .as(Checker.class)
                            .getGenerics();
            return byClass(generics[0].resolve());
        }
    }


}