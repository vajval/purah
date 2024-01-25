package com.purah.customAnn.matcher;

import com.google.common.collect.Sets;
import com.purah.base.FieldGetMethodUtil;
import com.purah.base.Name;
import com.purah.customAnn.ann.CNPhoneNum;
import com.purah.matcher.clazz.AbstractInstanceFieldMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;
import com.purah.springboot.ann.EnableOnPurahContext;
import com.purah.customAnn.ann.NotEmpty;
import com.purah.customAnn.ann.Range;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableOnPurahContext
@Name("custom_ann")
public class CustomAnnMatcher extends AbstractInstanceFieldMatcher {
    Set<Class<? extends Annotation>> customAnnList = Sets.newHashSet(NotEmpty.class, Range.class, CNPhoneNum.class);

    WildCardMatcher wildCardMatcher;

    public CustomAnnMatcher(String matchStr) {

        super(matchStr);
        wildCardMatcher = new WildCardMatcher(matchStr);

    }

    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {


        fieldGetMethodUtil = new FieldGetMethodUtil();
        List<String> result = new ArrayList<>();
        Map<Field, Method> fieldMethodMap = fieldGetMethodUtil.fieldGetMethodMap(clazz);


        for (Map.Entry<Field, Method> entry : fieldMethodMap.entrySet()) {
            Field field = entry.getKey();
            if(!wildCardMatcher.match(field.getName())){
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
