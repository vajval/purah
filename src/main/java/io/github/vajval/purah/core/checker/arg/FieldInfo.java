package io.github.vajval.purah.core.checker.arg;

import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class FieldInfo {
    Field field;
    List<Annotation> annList;

    String name;
    Class<?> clazz;

    public FieldInfo(Field field) {
        this.field = field;
        this.annList = Collections.unmodifiableList(Lists.newArrayList(field.getDeclaredAnnotations()));
        this.name = field.getName();
        this.clazz = field.getType();
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
