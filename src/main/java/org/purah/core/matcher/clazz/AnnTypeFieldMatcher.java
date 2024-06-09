package org.purah.core.matcher.clazz;


import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.base.Name;
import org.purah.core.matcher.ann.FieldType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Name("type_by_ann")
public class AnnTypeFieldMatcher extends AbstractClassCacheFieldMatcher   {


    public AnnTypeFieldMatcher(String matchStr) {
        super(matchStr);

    }


    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        List<String> result = new ArrayList<>();
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
