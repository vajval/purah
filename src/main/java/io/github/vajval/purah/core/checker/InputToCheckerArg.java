package io.github.vajval.purah.core.checker;


import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class InputToCheckerArg<INPUT_ARG> {
    // input need check arg
    INPUT_ARG arg;

    // user.id   user.childUser.id  child#0.id
    String fieldPath;


    // InputToCheckerArg.of(new People()) ->People.class
    // InputToCheckerArg.of(null) ->Object.class
    // InputToCheckerArg.of(null,String.class) ->String.class
    // InputToCheckerArg.of(new People(),String.class) ->People.class
    // use in reflect of null value
    Class<?> clazzInContext;

    final Field fieldInClass;

    //todo 组合模式
    List<Annotation> annListOnField = Collections.emptyList();
    List<Annotation> annListOnParam = Collections.emptyList();

    Map<String, Object> exDataMap = Collections.emptyMap();


    ITCArgNullType nullType = ITCArgNullType.null_value;

    private InputToCheckerArg(INPUT_ARG arg, Class<?> clazzInContext, String fieldPath) {
        this(arg, fieldPath, null, Collections.emptyList());
        this.arg = arg;
        if (clazzInContext == null) {
            clazzInContext = Object.class;
        }
        this.clazzInContext = clazzInContext;
    }

    private InputToCheckerArg(INPUT_ARG arg, String fieldPath, Field fieldInClass, List<Annotation> annListOnField) {
        this.arg = arg;
        this.fieldInClass = fieldInClass;
        if (this.fieldInClass != null) {
            this.clazzInContext = this.fieldInClass.getType();
        } else {
            this.clazzInContext = Object.class;
        }
        this.fieldPath = fieldPath;
        if (this.fieldPath == null) {
            this.fieldPath = "";
        }
        if (annListOnField != null) {
            this.annListOnField = annListOnField;
        }

    }


    public ITCArgNullType nullType() {
        if (this.arg != null) {
            return null;
        }
        return nullType;
    }


    public static <T> InputToCheckerArg<T> of(T INPUT_ARG) {
        return of(INPUT_ARG, Object.class);
    }

    public static <T> InputToCheckerArg<T> of(T INPUT_ARG, Class<?> clazzInContext) {
        return new InputToCheckerArg<>(INPUT_ARG, clazzInContext, "");
    }


    public static <T> InputToCheckerArg<T> createChild(T INPUT_ARG, String fieldPath) {
        return new InputToCheckerArg<>(INPUT_ARG, Object.class, fieldPath);
    }

    public static <T> InputToCheckerArg<T> createChildWithFieldConfig(T INPUT_ARG, String fieldPath, Field fieldInClass, List<Annotation> annotations) {
        return new InputToCheckerArg<>(INPUT_ARG, fieldPath, fieldInClass, annotations);
    }

    public static <T> InputToCheckerArg<T> createNullChildWithFieldConfig(String fieldPath, Field fieldInClass, List<Annotation> annotations, ITCArgNullType nullType) {
        InputToCheckerArg<T> result = new InputToCheckerArg<>(null, fieldPath, fieldInClass, annotations);
        result.nullType = nullType;
        return result;
    }


    public Field field() {
        return fieldInClass;
    }

    public String fieldPath() {
        if (fieldPath == null) return "";
        return fieldPath;
    }


    public <E extends Annotation> E annOnField(Class<E> clazz) {
        for (Annotation annotation : annListOnField) {
            if (annotation.annotationType().equals(clazz)) {
                return (E) annotation;
            }
        }
        return null;
    }

    public List<Annotation> annListOnField() {
        return annListOnField;
    }


    public Optional<INPUT_ARG> optional() {
        return Optional.of(arg);
    }

    public INPUT_ARG argValue() {
        return arg;
    }


    public Class<?> argClass() {
        if (arg != null) {
            return arg.getClass();
        }
        return clazzInContext;
    }

    public boolean isNull() {
        return arg == null;
    }

    @Override
    public String toString() {
        String annStr = "";
        if (!CollectionUtils.isEmpty(annListOnField)) {
            annStr = ", ann=" + annListOnField;
        }
        return "ITCArg{" + "field='" + fieldPath + '\'' + ", arg=" + arg + annStr + '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputToCheckerArg<?> that = (InputToCheckerArg<?>) o;
        return Objects.equals(arg, that.arg) && Objects.equals(fieldPath, that.fieldPath);
    }

    public boolean argEquals(Object o) {
        return Objects.equals(arg, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arg, fieldPath);
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public List<Annotation> getAnnListOnParam() {
        return annListOnParam;
    }

    public void setAnnListOnParam(List<Annotation> annListOnParam) {
        this.annListOnParam = annListOnParam;
    }

    public Map<String, Object> getExDataMap() {
        return exDataMap;
    }

    public void setExDataMap(Map<String, Object> exDataMap) {
        this.exDataMap = exDataMap;
    }
}


