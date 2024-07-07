package org.purah.core.matcher.extra;

import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.extra.clazz.AbstractClassCacheFieldMatcher;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelMatchInfo;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractCustomAnnMatcher extends AbstractClassCacheFieldMatcher implements MultilevelFieldMatcher {

    GeneralFieldMatcher generalFieldMatcher;

    public AbstractCustomAnnMatcher(String matchStr) {
        super(matchStr);
        generalFieldMatcher = new GeneralFieldMatcher(matchStr);
    }

    public abstract Set<Class<? extends Annotation>> customAnnList();

    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        Set<Class<? extends Annotation>> customAnnList = customAnnList();
        List<String> result = new ArrayList<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            String fieldName = propertyDescriptor.getName();
            if (!generalFieldMatcher.match(fieldName)) {
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
    public MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        return generalFieldMatcher.childFieldMatcher(inputArg,matchedField, childArg);
    }


}
