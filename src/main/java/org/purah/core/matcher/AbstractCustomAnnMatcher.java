package org.purah.core.matcher;

import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.matcher.clazz.AbstractClassCacheFieldMatcher;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.matcher.multilevel.GeneralMultilevelFieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelFieldMatcher;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractCustomAnnMatcher extends AbstractClassCacheFieldMatcher implements MultilevelFieldMatcher {

    GeneralMultilevelFieldMatcher generalMultilevelFieldMatcher;

    public AbstractCustomAnnMatcher(String matchStr) {
        super(matchStr);
        generalMultilevelFieldMatcher = new GeneralMultilevelFieldMatcher(matchStr);
    }

    public abstract Set<Class<? extends Annotation>> customAnnList();

    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        Set<Class<? extends Annotation>> customAnnList = customAnnList();
        List<String> result = new ArrayList<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            String fieldName = propertyDescriptor.getName();
            if (!generalMultilevelFieldMatcher.match(fieldName)) {
                continue;
            }
            Field declaredField;
            try {
                declaredField = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                continue;
            }
            for (Annotation declaredAnnotation : declaredField.getDeclaredAnnotations()) {

                if (customAnnList.contains(declaredAnnotation.annotationType())) {
                    result.add(fieldName);
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
