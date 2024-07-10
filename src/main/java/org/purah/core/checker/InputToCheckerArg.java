package org.purah.core.checker;


import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

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
    List<Annotation> annotations = Collections.emptyList();



    private InputToCheckerArg(INPUT_ARG arg, Class<?> clazzInContext, String fieldStr) {
        this(arg, fieldStr, null, Collections.emptyList());
        this.arg = arg;
        if (clazzInContext == null) {
            clazzInContext = Object.class;
//            throw new RuntimeException("不要将class设置为null,实在不行就Object.class");
        }
        this.clazzInContext = clazzInContext;

    }

    private InputToCheckerArg(INPUT_ARG arg, String fieldStr, Field fieldInClass, List<Annotation> annotations) {
        this.arg = arg;
        this.fieldInClass = fieldInClass;
        if (this.fieldInClass != null) {
            this.clazzInContext = this.fieldInClass.getType();

        } else {
            this.clazzInContext = Object.class;
        }
        this.fieldStr = fieldStr;
        if (this.fieldStr == null) {
            this.fieldStr = "";
        }
        if (annotations != null) {
            this.annotations = annotations;

        }
//        this.parent = parent;


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


    public Field field() {
        return fieldInClass;
    }

    public String fieldStr() {
        if (fieldStr == null) return "";
        return fieldStr;
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
        return "ITCArg{" + "fieldStr='" + fieldStr + '\'' + ", arg=" + arg + annStr + '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputToCheckerArg<?> that = (InputToCheckerArg<?>) o;
        return Objects.equals(arg, that.arg) && Objects.equals(fieldStr, that.fieldStr);
    }

    public boolean argEquals(Object o) {
        return Objects.equals(arg, o);

    }

    @Override
    public int hashCode() {
        return Objects.hash(arg, fieldStr);
    }

    public void setFieldStr(String fieldStr) {
        this.fieldStr = fieldStr;
    }
}


