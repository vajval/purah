package org.purah.core.matcher.clazz;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ClassFieldsCaches {

    Map<Class<?>, List<String>> clazzFieldMap = new ConcurrentHashMap<>();

    Function<Class<?>, List<String>> getFieldsFun;

    public ClassFieldsCaches(Function<Class<?>, List<String>> getFieldsFun) {
        this.getFieldsFun = getFieldsFun;
    }

    public List<String> getByInstanceClass(Class<?> clazz) {
        return clazzFieldMap.computeIfAbsent(clazz, c -> this.getFieldsFun.apply(c));
    }


}
