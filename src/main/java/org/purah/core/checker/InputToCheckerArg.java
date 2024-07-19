package org.purah.core.checker;


import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InputToCheckerArg<INPUT_ARG> {
    // input need check arg
    INPUT_ARG arg;
    //Relative fields to the root node.
    //example title  user.id   user.childUser.id
    String fullFieldName;

    // The class that can be determined within the context of the code.
    Class<?> clazzInContext;
    //If this object is a field of the parent object's class, store the field information here.
    final Field fieldInClass;
    // If this object is a field of the parent object's class, store the ann information here.
    List<Annotation> annotations = Collections.emptyList();

    ITCArgNullType nullType = ITCArgNullType.null_value;


    private InputToCheckerArg(INPUT_ARG arg, Class<?> clazzInContext, String fullFieldName) {
        this(arg, fullFieldName, null, Collections.emptyList());
        this.arg = arg;
        if (clazzInContext == null) {
            clazzInContext = Object.class;
        }
        this.clazzInContext = clazzInContext;
    }

    private InputToCheckerArg(INPUT_ARG arg, String fullFieldName, Field fieldInClass, List<Annotation> annotations) {
        this.arg = arg;
        this.fieldInClass = fieldInClass;
        if (this.fieldInClass != null) {
            this.clazzInContext = this.fieldInClass.getType();
        } else {
            this.clazzInContext = Object.class;
        }
        this.fullFieldName = fullFieldName;
        if (this.fullFieldName == null) {
            this.fullFieldName = "";
        }
        if (annotations != null) {
            this.annotations = annotations;
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


    public static <T> InputToCheckerArg<T> createChild(T INPUT_ARG, String childFieldStr) {
        return new InputToCheckerArg<>(INPUT_ARG, Object.class, childFieldStr);
    }

    public static <T> InputToCheckerArg<T> createChildWithFieldConfig(T INPUT_ARG, String childFieldStr, Field fieldInClass, List<Annotation> annotations) {
        return new InputToCheckerArg<>(INPUT_ARG, childFieldStr, fieldInClass, annotations);
    }

    public static <T> InputToCheckerArg<T> createNullChildWithFieldConfig(String childFieldStr, Field fieldInClass, List<Annotation> annotations, ITCArgNullType nullType) {
        InputToCheckerArg<T> result = new InputToCheckerArg<>(null, childFieldStr, fieldInClass, annotations);
        result.nullType = nullType;
        return result;
    }


    public Field field() {
        return fieldInClass;
    }

    public String fieldStr() {
        if (fullFieldName == null) return "";
        return fullFieldName;
    }


    public <E extends Annotation> E annOnField(Class<E> clazz) {
        Optional<Annotation> first = annotations.stream().filter(i -> i.annotationType().equals(clazz)).findFirst();
        return (E) (first.orElse(null));
    }

    public List<Annotation> annListOnField() {
        return annotations;
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
        if (!CollectionUtils.isEmpty(annotations)) {
            annStr = ", ann=" + annotations;
        }
        return "ITCArg{" + "field='" + fullFieldName + '\'' + ", arg=" + arg + annStr + '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputToCheckerArg<?> that = (InputToCheckerArg<?>) o;
        return Objects.equals(arg, that.arg) && Objects.equals(fullFieldName, that.fullFieldName);
    }

    public boolean argEquals(Object o) {
        return Objects.equals(arg, o);

    }

    @Override
    public int hashCode() {
        return Objects.hash(arg, fullFieldName);
    }

    public void setFullFieldName(String fullFieldName) {
        this.fullFieldName = fullFieldName;
    }

}


