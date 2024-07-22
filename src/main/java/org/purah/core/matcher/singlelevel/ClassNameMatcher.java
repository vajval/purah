package org.purah.core.matcher.singlelevel;


import org.apache.commons.beanutils.PropertyUtils;
import org.purah.core.name.Name;
import org.purah.core.exception.init.InitMatcherExceptionBase;
import org.purah.core.matcher.BaseStringMatcher;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/*
 * 获取有指定class的匹配器,不支持嵌套
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
            throw new InitMatcherExceptionBase("no class for name: " + className);
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


