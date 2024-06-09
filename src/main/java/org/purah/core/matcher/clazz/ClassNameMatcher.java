package org.purah.core.matcher.clazz;


import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.base.Name;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * 匹配指定 类名的字段
 */
@Name("class_name")
public class ClassNameMatcher extends AbstractClassCacheFieldMatcher   {


    Class<?> clazz;



    public ClassNameMatcher(String className) {
        super(className);
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {
        List<String> result = new ArrayList<>();
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            Class<?> returnType = propertyDescriptor.getReadMethod().getReturnType();
            if (returnType.equals(this.clazz)) {
                result.add(name);
            }
        }
        return result;
    }


}


