package com.purah.checker;

import com.google.common.collect.Lists;
import com.purah.checker.combinatorial.ExecType;
import org.checkerframework.checker.units.qual.C;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckInstance<INSTANCE> {
    INSTANCE instance;

    String fieldStr;

    Field fieldInClass;
    List<Annotation> annotations;

    ExecType.Main execType = ExecType.Main.all_success;

    public static CheckInstance copyAndNewExecType(CheckInstance source, ExecType.Main execType) {


        CheckInstance result = new CheckInstance(source.instance, source.fieldStr, source.fieldInClass,source.annotations);
        result.execType = execType;
        return result;
    }


    public static <T> CheckInstance<T> create(T instance, String fieldStr, Field fieldInClass, List<Annotation> annotations) {
        return new CheckInstance<>(instance, fieldStr, fieldInClass, annotations);
    }

    private CheckInstance(INSTANCE instance, String fieldStr, Field fieldInClass, List<Annotation> annotations) {
        this.instance = instance;
        this.fieldInClass = fieldInClass;
        this.fieldStr = fieldStr;

        this.annotations = annotations;
    }

    private CheckInstance(INSTANCE instance) {
        this.instance = instance;
    }

    public static <T> CheckInstance<T> create(T instance) {
        return new CheckInstance<>(instance);
    }

    public <E extends Annotation> E annOf(Class<E> clazz) {

        Optional<Annotation> first = annotations.stream().filter(i -> i.annotationType().equals(clazz)).findFirst();
        return (E) (first.orElse(null));
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public INSTANCE instance() {
        return instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckInstance<?> that = (CheckInstance<?>) o;
        return Objects.equals(instance, that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance);
    }


    public String fieldStr() {
        return fieldStr;
    }

    public void addFieldPreByParent(String Pre) {
        this.fieldStr = Pre + this.fieldStr;
    }

    public Field fieldInClass() {
        return fieldInClass;
    }

    @Override
    public String toString() {
        return "CheckInstance{" +
                "instance=" + instance +
                ", fieldStr='" + fieldStr + '\'' +
                ", fieldInClass=" + fieldInClass +
                ", annotations=" + annotations +
                '}';
    }
}


