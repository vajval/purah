package org.purah.core.checker.base;


import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckerException;
import org.purah.core.exception.PurahException;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对 同名  但是对支持的入参对象class不同的多个 checker封装
 * 例如两个 checker 都叫“checkId”
 * 但是一个支持的入参为String 另一个为Long
 * 这两个 会被封装到 typeEnableCheckerCacheMap中
 * 根据需要检查对象的
 *
 * @param <CHECK_INSTANCE>
 * @param <RESULT>
 */
public class ExecChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    String name;
    CheckClass defaultCheckClass;
    Checker<?, ?> defaultChecker;

    Map<CheckClass, Checker<?, ?>> typeEnableCheckerCacheMap = new ConcurrentHashMap<>();


    public ExecChecker(String name, Checker<?, ?> checker) {
        this.name = name;
        this.addNewChecker(checker);
    }


    public void addNewChecker(Checker<?, ?> checker) {
        if (defaultChecker == null) {
            this.defaultChecker = checker;
            this.defaultCheckClass = CheckClass.byChecker(checker);
        }
        CheckClass checkClass = CheckClass.byChecker(checker);
        this.typeEnableCheckerCacheMap.put(checkClass, checker);
        if (checkClass.equals(defaultCheckClass)) {
            defaultChecker = checker;
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult check(CheckInstance<CHECK_INSTANCE> checkInstance) {


        CheckClass checkClass = CheckClass.byInstance(checkInstance);
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
            return defaultChecker;
        }

        if (BaseLogicSupport(inputCheckClass)) return defaultChecker;
        Checker<?, ?> result = typeEnableCheckerCacheMap.get(inputCheckClass);
        if (result != null) return result;
        for (Map.Entry<CheckClass, Checker<?, ?>> entry : typeEnableCheckerCacheMap.entrySet()) {
            CheckClass entryKey = entry.getKey();
            if (support(inputCheckClass, entryKey)) {
                result = entry.getValue();
                typeEnableCheckerCacheMap.put(inputCheckClass, result);
                return result;
            }
        }
        return null;
    }

    protected boolean BaseLogicSupport(CheckClass checkClass) {
        return defaultCheckClass.support(checkClass);
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CheckClass that = (CheckClass) o;
            return Objects.equals(clazz, that.clazz);
        }


        @Override
        public int hashCode() {
            return Objects.hash(clazz);
        }

        static CheckClass byInstance(CheckInstance checkInstance
        ) {
            return byClass(checkInstance.instanceClass());
        }

        static CheckClass byClass(Class<?> clazz) {
            CheckClass checkClass = new CheckClass();
            if (clazz == null) clazz = Object.class;
            checkClass.clazz = clazz;
            return checkClass;
        }

        static CheckClass byChecker(Checker<?, ?> checker) {
            return byClass(checker.inputCheckInstanceClass());
        }

        @Override
        public String toString() {
            return "CheckClass{" +
                    "clazz=" + clazz +
                    '}';
        }
    }


}