package org.purah.core.matcher.clazz;


import org.purah.core.base.FieldGetMethodUtil;
import org.purah.core.base.Name;
import org.purah.core.matcher.intf.FieldMatcherWithInstance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 匹配指定 类名的字段
 */
@Name("class_name")
public class ClassNameMatcher extends AbstractInstanceFieldMatcher implements FieldMatcherWithInstance {


    Class<?> clazz;
    FieldGetMethodUtil fieldGetMethodUtil;


    public ClassNameMatcher(String className) {
        super(className);
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {

            throw new RuntimeException(e);
        }
        fieldGetMethodUtil = new FieldGetMethodUtil();

    }


    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {

        List<String> result = new ArrayList<>();
        Map<Field, Method> fieldMethodMap = fieldGetMethodUtil.fieldGetMethodMap(clazz);
        for (Field field : fieldMethodMap.keySet()) {
            Class<?> type = field.getType();
            if (type.equals(this.clazz)) {
                result.add(field.getName());
            }
        }
        return result;
    }


}


