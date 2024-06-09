package org.purah.core.checker.base;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InputCheckArg<INSTANCE> {
    //需要检查的对象
    INSTANCE inputArg;
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

    private InputCheckArg(INSTANCE inputArg, Class<?> clazzInContext, String fieldStr, Object parent) {
        this(inputArg, fieldStr, null, Collections.emptyList(), null);
        this.inputArg = inputArg;
        if (clazzInContext == null) {
            throw new RuntimeException("不要将class设置为null,实在不行就Object.class");
        }
        this.clazzInContext = clazzInContext;
        this.parent = parent;

    }

    private InputCheckArg(INSTANCE inputArg, String fieldStr, Field fieldInClass, List<Annotation> annotations, Object parent) {
        this.inputArg = inputArg;
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


    public static <T> InputCheckArg<T> create(T instance) {
        return create(instance, Object.class);
    }

    public static <T> InputCheckArg<T> create(T instance, Class<?> clazzInContext) {
        return create(instance, clazzInContext, "root", null);
    }

    public static <T> InputCheckArg<T> create(T instance, Class<?> clazzInContext, String fieldStr, Object parent) {
        return new InputCheckArg<>(instance, clazzInContext, fieldStr, parent);
    }

    public static <T> InputCheckArg<T> createChildWithFieldConfig(T instance, Field fieldInClass, List<Annotation> annotations, Object parent) {
        return new InputCheckArg<>(instance, fieldInClass.getName(), fieldInClass, annotations, parent);
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


    public INSTANCE inputArg() {
        return inputArg;
    }


    public Class<?> inputArgClass() {
        if (inputArg != null) {
            return inputArg.getClass();
        }
        return clazzInContext;

    }

    @Override
    public String toString() {
        return "CheckInputArg{" +
                "inputArg=" + inputArg +
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
        InputCheckArg<?> that = (InputCheckArg<?>) o;
        return Objects.equals(inputArg, that.inputArg) && Objects.equals(fieldStr, that.fieldStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputArg, fieldStr);
    }


}


