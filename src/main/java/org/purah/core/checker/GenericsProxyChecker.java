package org.purah.core.checker;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.exception.CheckException;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.exception.init.InitCheckerException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 public static Checker<Long, Object> longChecker = LambdaChecker.of(Long.class).build("id1", i -> i == 1L);
 public static Checker<Integer, Object> intChecker = LambdaChecker.of(Integer.class).build("id1", i -> i == 1);
 genericsProxyChecker.addNewChecker(longChecker); genericsProxyChecker.addNewChecker(intChecker);
 genericsProxyChecker.check(1)//use intChecker
 genericsProxyChecker.check(1L)//use longChecker
 */
public class GenericsProxyChecker implements Checker<Object, Object> {


    final String name;
    InputArgClass defaultInputArgClass;
    Checker<?, ?> defaultChecker;
    final Map<InputArgClass, Checker<?, ?>> cacheGenericsCheckerMapping = new ConcurrentHashMap<>();

    BiFunction<GenericsProxyChecker, Integer, Integer> tryUpdateContext;
    int checkerFactoryCount;

    private GenericsProxyChecker(String name) {
        this.name = name;
    }

    private GenericsProxyChecker(String name, int checkerFactoryCount, BiFunction<GenericsProxyChecker, Integer, Integer> tryUpdateContext) {
        this.name = name;
        this.checkerFactoryCount = checkerFactoryCount;
        this.tryUpdateContext = tryUpdateContext;

    }

    public static GenericsProxyChecker create(String name) {
        return new GenericsProxyChecker(name);

    }

    public static GenericsProxyChecker createByChecker(Checker<?, ?> checker) {
        return create(checker.name()).addNewChecker(checker);

    }

    public static GenericsProxyChecker createAndSupportUpdateByCheckerFactory(String name, int checkerFactoryCount, BiFunction<GenericsProxyChecker, Integer, Integer> tryUpdateContext) {
        return new GenericsProxyChecker(name, checkerFactoryCount, tryUpdateContext);

    }



    protected GenericsProxyChecker addNewChecker(Checker<?, ?> checker) {
        if (checker == null) {
            throw new InitCheckerException("checker cannot be null");
        }
        if (checker instanceof GenericsProxyChecker) {
            throw new InitCheckerException("GenericsProxyChecker no nested support");
        }
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

        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CheckResult<Object> check(InputToCheckerArg<Object> inputToCheckerArg) {

        Checker<?, ?> checker = getChecker(inputToCheckerArg);

        if (checker == null) {

            InputArgClass inputCheckInstanceArgClass = InputArgClass.byInstance(inputToCheckerArg);

            throw new CheckException(this, "checker [" + this.name + "] not support class " + inputCheckInstanceArgClass.clazz);
        }
        return ((Checker) checker).check(inputToCheckerArg);
    }


    protected Checker<?, ?> getChecker(InputToCheckerArg<Object> inputToCheckerArg) {

        if (inputToCheckerArg == null) {
            return defaultChecker;
        }
        if (inputToCheckerArg.isNull() && (inputToCheckerArg.argClass() == null || inputToCheckerArg.argClass().equals(Object.class))) {
            return defaultChecker;
        }
        InputArgClass inputCheckInstanceArgClass = InputArgClass.byInstance(inputToCheckerArg);


        int oldCount = this.checkerFactoryCount;
        Checker<?, ?> result = getCheckerBySupportClass(inputCheckInstanceArgClass);
        if (result != null) {
            return result;
        }
        tryUpdateContext();
        if (oldCount != this.checkerFactoryCount) {
            result = getCheckerBySupportClass(inputCheckInstanceArgClass);
        }


        if (result != null) {
            return result;
        }


        InputArgClass convertClass = convert(inputToCheckerArg);
        if (convertClass == null) {
            return null;
        }
        Checker<?, ?> checkerByConvertWrapperClass = getCheckerBySupportClass(convertClass);
        if (checkerByConvertWrapperClass == null) {
            return null;
        }
        cacheGenericsCheckerMapping.put(inputCheckInstanceArgClass, checkerByConvertWrapperClass);
        return checkerByConvertWrapperClass;


    }

    private static final BiMap<Class<?>, Class<?>> wrapperClassMap = buildWrapperClassMap();

    private static BiMap<Class<?>, Class<?>> buildWrapperClassMap() {

        BiMap<Class<?>, Class<?>> wrapperClassMap = HashBiMap.create();

        wrapperClassMap.put(byte.class, Byte.class);
        wrapperClassMap.put(short.class, Short.class);
        wrapperClassMap.put(int.class, Integer.class);
        wrapperClassMap.put(long.class, Long.class);
        wrapperClassMap.put(char.class, Character.class);
        wrapperClassMap.put(boolean.class, Boolean.class);
        wrapperClassMap.put(double.class, Double.class);
        wrapperClassMap.put(float.class, Float.class);
        return wrapperClassMap;
    }

    private static InputArgClass convert(InputToCheckerArg<Object> inputToCheckerArg) {
        Class<?> clazz = inputToCheckerArg.argClass();

        if (inputToCheckerArg.argValue() == null) {
            if (wrapperClassMap.containsValue(clazz)) {
                return null;
            }
        }
        Class<?> resultClass = wrapperClassMap.get(clazz);
        if (resultClass == null) {
            resultClass = wrapperClassMap.inverse().get(clazz);
        }
        if (resultClass == null) {
            return null;
        }
        return new InputArgClass(resultClass);


    }


    protected Checker<?, ?> getCheckerBySupportClass(InputArgClass inputCheckInstanceArgClass) {

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


        return null;

    }

    private void tryUpdateContext() {
        if (tryUpdateContext == null) return;
        synchronized (this) {
            this.checkerFactoryCount = tryUpdateContext.apply(this, this.checkerFactoryCount);
        }
    }


    static class InputArgClass {

        Class<?> clazz;

        private InputArgClass(Class<?> clazz) {
            if (clazz == null) {
                throw new UnexpectedException("InputArgClass class cannnot null");
            }
            this.clazz = clazz;
        }

        public static InputArgClass byChecker(Checker<?, ?> checker) {
            Class<?> clazz = checker.inputArgClass();
            if (clazz == null) {
                return new InputArgClass(Object.class);
            }
            return new InputArgClass(clazz);
        }

        public static InputArgClass byInstance(InputToCheckerArg<?> inputToCheckerArg) {
            return new InputArgClass(inputToCheckerArg.argClass());
        }


        public boolean support(InputArgClass inputInputArgClass) {
            if (this == inputInputArgClass) return true;
            if (this.clazz == inputInputArgClass.clazz) return true;
            if (inputInputArgClass.clazz == null) {
                return false;
            }
            /*
             能处理Map 就一定能处理 HashMap                 吧
             The ability to handle `Map` implies the ability to handle `HashMap`.
             *
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