package org.purah.core.checker;


import org.purah.core.checker.combinatorial.ExecType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CheckInstance<INSTANCE> {
    INSTANCE instance;

    String fieldStr;

    Field fieldInClass;
    List<Annotation> annotations;

    ExecType.Main execType = ExecType.Main.all_success;


    public String logStr() {
        return "instance";
    }

    public static CheckInstance copyAndNewExecType(CheckInstance source, ExecType.Main execType) {


        CheckInstance result = new CheckInstance(source.instance, source.fieldStr, source.fieldInClass, source.annotations);
        result.execType = execType;
        return result;
    }


    public static <T> CheckInstance<T> createWithConfig(T instance, String fieldStr, Field fieldInClass, List<Annotation> annotations) {
        return new CheckInstance<>(instance, fieldStr, fieldInClass, annotations);
    }

    private CheckInstance(INSTANCE instance, String fieldStr, Field fieldInClass, List<Annotation> annotations) {
        this.instance = instance;
        this.fieldInClass = fieldInClass;
        this.fieldStr = fieldStr;
//        if (fieldStr != null) {
//            this.fieldStr = "root." + fieldStr;
//        } else {
//            this.fieldStr = "root";
//        }


        this.annotations = annotations;
    }

    private CheckInstance(INSTANCE instance) {
        this.instance = instance;
    }

    public static <T> CheckInstance<T> create(T instance) {
        CheckInstance<T> checkInstance = new CheckInstance<>(instance);
        checkInstance.fieldStr = "root";
        return checkInstance;

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
        if (fieldStr == null) return "";
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


