package org.purah.core.checker.base;


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
    String fieldStr;

    // 参数在代码中明确使用的的class，inputArg为null时，inputArgClass()返回此class
    Class<?> clazzInContext;
    // 如果这个对象是父对象class的一个field ，此处保存field信息

    Field fieldInClass;
    // 如果这个对象是父对象class的一个field ，此处保存field中的注解
    List<Annotation> annotations;
    Object parent;

    private InputToCheckerArg(INPUT_ARG arg, Class<?> clazzInContext, String fieldStr, Object parent) {
        this(arg, fieldStr, null, Collections.emptyList(), null);
        this.arg = arg;
        if (clazzInContext == null) {
            throw new RuntimeException("不要将class设置为null,实在不行就Object.class");
        }
        this.clazzInContext = clazzInContext;
        this.parent = parent;

    }

    private InputToCheckerArg(INPUT_ARG arg, String fieldStr, Field fieldInClass, List<Annotation> annotations, Object parent) {
        this.arg = arg;
        this.fieldInClass = fieldInClass;
        if (this.fieldInClass != null) {
            this.clazzInContext = this.fieldInClass.getType();
        }
        this.fieldStr = fieldStr;
        if (this.fieldStr == null) {
            this.fieldStr = "";
        }
        this.annotations = annotations;
        this.parent = parent;


    }


    public static <T> InputToCheckerArg<T> create(T INPUT_ARG) {
        return create(INPUT_ARG, Object.class);
    }

    public static <T> InputToCheckerArg<T> create(T INPUT_ARG, Class<?> clazzInContext) {
        return create(INPUT_ARG, clazzInContext, "", null);
    }

    public static <T> InputToCheckerArg<T> create(T INPUT_ARG, Class<?> clazzInContext, String fieldStr, Object parent) {
        return new InputToCheckerArg<>(INPUT_ARG, clazzInContext, fieldStr, parent);
    }

    public static <T> InputToCheckerArg<T> createChildWithFieldConfig(T INPUT_ARG, Field fieldInClass, List<Annotation> annotations, Object parent) {
        return new InputToCheckerArg<>(INPUT_ARG, fieldInClass.getName(), fieldInClass, annotations, parent);
    }



    public Object parent() {
        return parent;
    }

    public <E> Object parent(Class<E> clazz) {
        return clazz.cast(parent);
    }

    public String fieldStr() {
        if (fieldStr == null) return "";
        return fieldStr;
    }

    public void addFieldPreByParent(String Pre) {
        this.fieldStr = Pre + this.fieldStr;
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

    @Override
    public String toString() {
        return "CheckInputArg{" +
                "inputArg=" + arg +
                ", fieldStr='" + fieldStr + '\'' +
                ", clazzInContext=" + clazzInContext +
                ", fieldInClass=" + fieldInClass +
                ", annotations=" + annotations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputToCheckerArg<?> that = (InputToCheckerArg<?>) o;
        return Objects.equals(arg, that.arg) && Objects.equals(fieldStr, that.fieldStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arg, fieldStr);
    }


}


