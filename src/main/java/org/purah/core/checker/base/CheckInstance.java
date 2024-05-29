package org.purah.core.checker.base;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CheckInstance<INSTANCE> {
    //需要检查的对象
    INSTANCE instance;
    //相对于根节点的字段
    //例如 title  user.id   user.childUser.id  等
    String fieldStr;

    // instance的class
    Class<?> clazzInContext;
    // 如果这个对象是父对象class的一个field ，此处保存field信息

    Field fieldInClass;
    // 如果这个对象是父对象class的一个field ，此处保存field中的注解
    List<Annotation> annotations;

    private CheckInstance(INSTANCE instance, Class<?> clazzInContext) {
        this(instance, "root", null, Collections.emptyList());
        this.instance = instance;
        if (clazzInContext == null) {
            throw new RuntimeException("不要将class设置为null,实在不行就Object.class");
        }
        this.clazzInContext = clazzInContext;

    }

    private CheckInstance(INSTANCE instance, String fieldStr, Field fieldInClass, List<Annotation> annotations) {
        this.instance = instance;
        this.fieldInClass = fieldInClass;
        if (this.fieldInClass != null) {
            this.clazzInContext = this.fieldInClass.getType();
        }
        this.fieldStr = fieldStr;
        if (this.fieldStr == null) {
            this.fieldStr = "";
        }
        this.annotations = annotations;


    }


    public static <T> CheckInstance<T> createObjectInstance(T instance) {
        return create(instance, Object.class);
    }

    public static <T> CheckInstance<T> create(T instance, Class<?> clazzInContext) {
        return new CheckInstance<>(instance, clazzInContext);
    }


    public static <T> CheckInstance<T> createWithFieldConfig(T instance, String fieldStr, Field fieldInClass, List<Annotation> annotations) {
        return new CheckInstance<>(instance, fieldStr, fieldInClass, annotations);
    }


    public String fieldStr() {
        if (fieldStr == null) return "";
        return fieldStr;
    }

    public void addFieldPreByParent(String Pre) {
        this.fieldStr = Pre + this.fieldStr;
    }


    public <E extends Annotation> E annOf(Class<E> clazz) {
        Optional<Annotation> first = annotations.stream().filter(i -> i.annotationType().equals(clazz)).findFirst();
        return (E) (first.orElse(null));
    }

    public List<Annotation> annListOnField() {
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

    public Class<?> instanceClass() {
        if (instance != null) {
            return instance.getClass();
        }
        return clazzInContext;

    }


    @Override
    public int hashCode() {
        return Objects.hash(instance);
    }


    @Override
    public String toString() {
        return "CheckInstance{" +
                "instance=" + instance +
                ", fieldStr='" + fieldStr + '\'' +
                ", clazzInContext=" + clazzInContext +
                ", fieldInClass=" + fieldInClass +
                ", annotations=" + annotations +
                '}';
    }
}


