package com.purah.matcher;


import com.purah.base.NameUtil;
import com.purah.exception.FieldMatcherException;
import com.purah.matcher.factory.MatcherFactory;
import com.purah.matcher.intf.FieldMatcher;

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

//
//    /**
//     * 建议继承类 {@link BaseStringMatcher}
//     * 对于输入的类<p>
//     * 1 必须有一个 只有String 作为入参的单参非私有有构造器<p>
//     * 2 类上必须有 {@link com.vajva.ann.Name} 注解<p>
//     */

    public void initVerify(Class<? extends FieldMatcher> fieldMatcherClazz) {
        try {
            constructor = fieldMatcherClazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw new FieldMatcherException(fieldMatcherClazz.getName() + "没有可用的构造器,本方法只支持只有一个String入参的构造方法");
        }
        name = NameUtil.nameByClassNameAnn(fieldMatcherClazz);
        this.fieldMatcherClazz = fieldMatcherClazz;
    }

    @Override
    public FieldMatcher create(String matchStr) {
        try {
            return constructor.newInstance(matchStr);
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            //todo
            throw new RuntimeException(e);
        }

    }

    @Override
    public String name() {
        return name;
    }
}
