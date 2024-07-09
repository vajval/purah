package org.purah.core.matcher.singlelevel;


import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.base.Name;
import org.purah.core.exception.init.InitMatcherException;
import org.purah.core.matcher.BaseStringMatcher;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Match based on the class name of the field
 * Just an example
 * People{
 * String name;
 * Long id;
 * }
 * new ClassNameMatcher("java.lang.String")-> new People(name:name,id:123)
 * <p>
 * return {id:123}
 */
@Name("class_name")
public class ClassNameMatcher extends BaseStringMatcher {


    Class<?> clazz;


    public ClassNameMatcher(String className) {
        super(className);
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new InitMatcherException("no class for name: " + className);
        }

    }

    @Override
    public boolean match(String field, Object belongInstance) {
        Set<String> allFields = getFieldsByClass(belongInstance.getClass());
        return allFields.contains(field);
    }



    public Set<String> getFieldsByClass(Class<?> clazz) {
        Set<String> result =  new HashSet<>();
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

