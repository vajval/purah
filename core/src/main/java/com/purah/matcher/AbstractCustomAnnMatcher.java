package com.purah.matcher;

import com.purah.base.FieldGetMethodUtil;
import com.purah.matcher.clazz.AbstractInstanceFieldMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCustomAnnMatcher extends AbstractInstanceFieldMatcher {
//    Set<Class<? extends Annotation>> customAnnList = Sets.newHashSet(NotEmpty.class, Range.class, CNPhoneNum.class);

    WildCardMatcher wildCardMatcher;

    public AbstractCustomAnnMatcher(String matchStr) {

        super(matchStr);
        wildCardMatcher = new WildCardMatcher(matchStr);

    }

    public abstract Set<Class<? extends Annotation>> customAnnList();

    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {


        fieldGetMethodUtil = new FieldGetMethodUtil();
        List<String> result = new ArrayList<>();
        Map<Field, Method> fieldMethodMap = fieldGetMethodUtil.fieldGetMethodMap(clazz);

        Set<Class<? extends Annotation>> customAnnList = customAnnList();
        for (Map.Entry<Field, Method> entry : fieldMethodMap.entrySet()) {
            Field field = entry.getKey();
            if (!wildCardMatcher.match(field.getName())) {
                continue;
            }
            for (Annotation declaredAnnotation : field.getDeclaredAnnotations()) {

                if (customAnnList.contains(declaredAnnotation.annotationType())) {
                    result.add(field.getName());
                    break;
                }

            }
        }
        return result;
    }
}
