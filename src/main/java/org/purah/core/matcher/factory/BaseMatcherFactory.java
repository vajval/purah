package org.purah.core.matcher.factory;


import org.purah.core.base.NameUtil;
import org.purah.core.exception.FieldMatcherException;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * 最基础的字段匹配器工厂，可以通过输入class 来创建简单的字段匹配器<p>
 * <p>
 * 对于输入的class参数的要求，详情见
 * {@linkplain #initVerify(Class) initVerify 函数}
 */
public class BaseMatcherFactory implements MatcherFactory {

    protected String name;
    protected Class<? extends FieldMatcher> fieldMatcherClazz;
    protected Constructor<? extends FieldMatcher> constructor;

    public BaseMatcherFactory(Class<? extends FieldMatcher> fieldMatcherClazz) {
        initVerify(fieldMatcherClazz);
    }


    /**
     * 建议继承类 {@link }
     * 对于输入的类<p>
     * 1 必须有一个 只有String 作为入参的单参非私有有构造器<p>
     * 2 类上必须有 {@link} 注解<p>
     */

    public void initVerify(Class<? extends FieldMatcher> fieldMatcherClazz) {
        constructor = singleStringConstructor(fieldMatcherClazz);
        if (constructor == null) {
            throw new FieldMatcherException(fieldMatcherClazz.getName() + "没有可用的构造器,本方法只支持只有一个String入参的构造方法");

        }
        name = NameUtil.nameByAnnOnClass(fieldMatcherClazz);
        this.fieldMatcherClazz = fieldMatcherClazz;
    }

    public static Constructor<? extends FieldMatcher> singleStringConstructor(Class<? extends FieldMatcher> fieldMatcherClazz) {
        try {
            return fieldMatcherClazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static boolean clazzVerify(Class<? > clazz) {
        if (BaseStringMatcher.class.isAssignableFrom(clazz)) {
            Constructor<? extends FieldMatcher> constructor = singleStringConstructor((Class)clazz);
            return constructor != null;
        }
        return false;

    }

    @Override
    public FieldMatcher create(String matchStr) {
        try {
            return constructor.newInstance(matchStr);
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            //todo
            throw new FieldMatcherException(e);
        }

    }

    @Override
    public String name() {
        return name;
    }
}
