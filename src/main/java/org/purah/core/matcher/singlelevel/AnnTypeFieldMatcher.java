package org.purah.core.matcher.singlelevel;


import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.base.Name;
import org.purah.core.matcher.BaseStringMatcher;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * People{
 * @FieldType("1") String name;
 * @FieldType("2") String id;
 * }
 *
 * <p>
 * new AnnTypeFieldMatcher("1")-> new People(name:vajval,id:123)
 * <p>
 * return {name:vajval}
 */
@Name("type_by_ann")

public class AnnTypeFieldMatcher extends BaseStringMatcher {


    public AnnTypeFieldMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        Set<String> allFields = getFieldsByClass(belongInstance.getClass());
        return allFields.contains(field);
    }


    public Set<String> getFieldsByClass(Class<?> clazz) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        Set<String> result = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            Field declaredField;
            try {
                declaredField = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                continue;
            }
            FieldType fieldType = declaredField.getDeclaredAnnotation(FieldType.class);
            if (fieldType != null) {
                String[] strings = fieldType.value();
                for (String value : strings) {
                    if (value.equals(this.matchStr)) {
                        result.add(declaredField.getName());
                        break;
                    }
                }
            }
        }
        return result;
    }


}
