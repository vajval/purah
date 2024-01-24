package com.purah.checker;

import com.google.common.collect.Lists;

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


    Field fieldInClass;
    List<Annotation> annotations;


    public static <T> CheckInstance<T> create(T instance, Field fieldInClass, Annotation[] annotations) {
        return new CheckInstance<>(instance, fieldInClass, annotations);
    }

    private CheckInstance(INSTANCE instance, Field fieldInClass, Annotation[] annotations) {
        this.instance = instance;
        this.fieldInClass = fieldInClass;
        this.annotations = Stream.of(annotations).collect(Collectors.toList());
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
}


