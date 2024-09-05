package io.github.vajval.purah.core.checker.arg;

import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;

public class ParamInfo {
    Parameter parameter;

    List<Annotation> annList;

    String name;
    Class<?> clazz;

    public ParamInfo(Parameter parameter) {
        this.parameter = parameter;
        this.annList = Collections.unmodifiableList(Lists.newArrayList(parameter.getDeclaredAnnotations()));
        this.name = parameter.getName();
        this.clazz = parameter.getType();
    }

    public List<Annotation> annList() {
        return annList;
    }

    public String name() {
        return name;
    }

    public String clazz() {
        return name;
    }
}
