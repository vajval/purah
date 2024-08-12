package io.github.vajval.purah.core.matcher.singlelevel;


import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.name.Name;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
 * 获取有注解字段的匹配器,不支持嵌套
 * People{
 *
 * @FieldType("1") String name;
 * @FieldType("1") String address;
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
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        if (belongInstance == null) {
            return new HashSet<>();
        }
        Set<String> allFields = getFieldsByClass(belongInstance.getClass());
        Set<String> result = new HashSet<>();
        for (String field : fields) {
            for (String matchKey : allFields) {
                if (Objects.equals(field, matchKey)) {
                    result.add(field);
                    break;
                }

            }
        }
        return result;
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
