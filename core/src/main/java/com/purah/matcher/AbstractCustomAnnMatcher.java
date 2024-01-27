package com.purah.matcher;

import com.purah.base.FieldGetMethodUtil;
import com.purah.matcher.clazz.AbstractInstanceFieldMatcher;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.matcher.multilevel.GeneralMultilevelFieldMatcher;
import com.purah.matcher.multilevel.MultilevelFieldMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCustomAnnMatcher extends AbstractInstanceFieldMatcher implements MultilevelFieldMatcher {
//    Set<Class<? extends Annotation>> customAnnList = Sets.newHashSet(NotEmpty.class, Range.class, CNPhoneNum.class);

    GeneralMultilevelFieldMatcher generalMultilevelFieldMatcher;

    public AbstractCustomAnnMatcher(String matchStr) {

        super(matchStr);
        generalMultilevelFieldMatcher = new GeneralMultilevelFieldMatcher(matchStr);

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
            if (!generalMultilevelFieldMatcher.match(field.getName())) {
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

    @Override
    public FieldMatcher childFieldMatcher(String matchedField) {
        return generalMultilevelFieldMatcher.childFieldMatcher(matchedField);
    }
}
