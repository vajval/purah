package org.purah.core.matcher;

import org.purah.core.base.FieldGetMethodUtil;
import org.purah.core.matcher.clazz.AbstractInstanceFieldMatcher;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.matcher.multilevel.GeneralMultilevelFieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelFieldMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCustomAnnMatcher extends AbstractInstanceFieldMatcher implements MultilevelFieldMatcher {

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
