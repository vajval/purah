package org.purah.core.checker.base;


import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckerException;
import org.purah.core.exception.PurahException;

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
public class GenericsProxyChecker<CHECK_INSTANCE, RESULT> implements Checker<CHECK_INSTANCE, RESULT> {


    String name;
    InputArgClass defaultInputArgClass;
    Checker<?, ?> defaultChecker;
    Map<InputArgClass, Checker<?, ?>> cacheGenericsCheckerMapping = new ConcurrentHashMap<>();


    public GenericsProxyChecker(String name, Checker<?, ?> checker) {
        this.name = name;
        this.addNewChecker(checker);
    }

    /**
     * 添加新的
     */

    public void addNewChecker(Checker<?, ?> checker) {
        InputArgClass checkerSupportInputArgClass = InputArgClass.byChecker(checker);
        if (defaultChecker == null) {
            this.defaultChecker = checker;
            this.defaultInputArgClass = checkerSupportInputArgClass;
        } else {
            if (checkerSupportInputArgClass.equals(defaultInputArgClass)) {
                defaultChecker = checker;
            }
        }

        this.cacheGenericsCheckerMapping.put(checkerSupportInputArgClass, checker);


    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult check(InputCheckArg<CHECK_INSTANCE> inputCheckArg) {


        Checker<?, ?> checker = getChecker(inputCheckArg);
        try {
            return ((Checker) checker).check(inputCheckArg);
        } catch (PurahException exception) {
            throw exception;
        }
    }

    protected Checker<?, ?> getChecker(InputCheckArg<CHECK_INSTANCE> inputCheckArg) {
        InputArgClass inputCheckInstanceArgClass = InputArgClass.byInstance(inputCheckArg);

        if (defaultInputArgClass.support(inputCheckInstanceArgClass)) {
            return defaultChecker;
        }

        Checker<?, ?> result = cacheGenericsCheckerMapping.get(inputCheckInstanceArgClass);
        if (result != null) {
            return result;
        }
        for (Map.Entry<InputArgClass, Checker<?, ?>> entry : cacheGenericsCheckerMapping.entrySet()) {
            InputArgClass cacheInputArgClass = entry.getKey();

            if (cacheInputArgClass.support(inputCheckInstanceArgClass)) {
                result = entry.getValue();
                cacheGenericsCheckerMapping.put(inputCheckInstanceArgClass, result);
                return result;
            }
        }
        throw new CheckerException(this, "checker " + this.name + "没有对该类的解析方法" + inputCheckInstanceArgClass.clazz);


    }


    static class InputArgClass {

        Class<?> clazz;

        private InputArgClass(Class<?> clazz) {
            if (clazz == null) {
                throw new RuntimeException("不该出现这个错误");
            }
            this.clazz = clazz;
        }

        public static InputArgClass byChecker(Checker<?, ?> checker) {
            Class<?> clazz = checker.inputCheckInstanceClass();
            if (clazz == null) {
                return new InputArgClass(Object.class);
            }
            return new InputArgClass(clazz);
        }

        public static InputArgClass byInstance(InputCheckArg<?> inputCheckArg) {
            return new InputArgClass(inputCheckArg.inputArgClass());
        }


        public boolean support(InputArgClass inputInputArgClass) {
            if (this == inputInputArgClass) return true;
            if (this.clazz == inputInputArgClass.clazz) return true;
            if (inputInputArgClass.clazz == null) {
                return false;
            }
            /*
             * 能处理Map 就一定能处理 HashMap
             */
            return this.clazz.isAssignableFrom(inputInputArgClass.clazz);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InputArgClass that = (InputArgClass) o;
            return Objects.equals(clazz, that.clazz);
        }


        @Override
        public int hashCode() {
            return Objects.hash(clazz);
        }

        @Override
        public String toString() {
            return "InputArgClass{" +
                    "clazz=" + clazz +
                    '}';
        }
    }


}