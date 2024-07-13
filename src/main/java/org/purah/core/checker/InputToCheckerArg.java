package org.purah.core.checker;


import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InputToCheckerArg<INPUT_ARG> {
    //需要检查的对象
    INPUT_ARG arg;
    //相对于根节点的字段
    //例如 title  user.id   user.childUser.id  等
    String fullFieldName;

    // 参数在代码中明确使用的的class，inputArg为null时，inputArgClass()返回此class
    Class<?> clazzInContext;
    // 如果这个对象是父对象class的一个field ，此处保存field信息

    Field fieldInClass;
    // 如果这个对象是父对象class的一个field ，此处保存field中的注解
    List<Annotation> annotations = Collections.emptyList();
    ITCArgNullType nullType = ITCArgNullType.null_value;


    private InputToCheckerArg(INPUT_ARG arg, Class<?> clazzInContext, String fullFieldName) {
        this(arg, fullFieldName, null, Collections.emptyList());
        this.arg = arg;
        if (clazzInContext == null) {
            clazzInContext = Object.class;
//            throw new RuntimeException("不要将class设置为null,实在不行就Object.class");
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
//        this.parent = parent;


    }

    public static <T> InputToCheckerArg<T> nullForClazz(ITCArgNullType itcArgNullType, Class<?> clazzInContext) {
        InputToCheckerArg<T> result = nullForClazz(clazzInContext);
        result.nullType = itcArgNullType;
        return result;
    }

    public ITCArgNullType nullType() {
        if (this.arg != null) {
            return null;
        }
        return nullType;
    }

    public static <T> InputToCheckerArg<T> nullForClazz(Class<?> clazzInContext) {
        return of(null, clazzInContext);
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

    public static <T> InputToCheckerArg<T> createNullChildWithFieldConfig(String childFieldStr, Field fieldInClass, List<Annotation> annotations,ITCArgNullType nullType) {
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
        return "ITCArg{" + "fullFieldName='" + fullFieldName + '\'' + ", arg=" + arg + annStr + '}';
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


