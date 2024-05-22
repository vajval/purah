package org.purah.core.matcher.clazz;



import org.purah.core.base.FieldGetMethodUtil;
import org.purah.core.base.Name;
import org.purah.core.matcher.ann.FieldType;
import org.purah.core.matcher.intf.FieldMatcherWithInstance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Name("type_by_ann")
public class AnnTypeFieldMatcher extends AbstractInstanceFieldMatcher implements FieldMatcherWithInstance {

    protected FieldGetMethodUtil fieldGetMethodUtil;

    public AnnTypeFieldMatcher(String matchStr) {
        super(matchStr);

    }


    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {
        fieldGetMethodUtil = new FieldGetMethodUtil();
        List<String> result = new ArrayList<>();
        Map<Field, Method> fieldMethodMap = fieldGetMethodUtil.fieldGetMethodMap(clazz);
        for (Map.Entry<Field, Method> entry : fieldMethodMap.entrySet()) {
            Field field = entry.getKey();
            FieldType fieldType = field.getDeclaredAnnotation(FieldType.class);
            if (fieldType != null) {
                String[] strings = fieldType.value();
                for (String value : strings) {
                    if (value.equals(this.matchStr)) {
                        result.add(field.getName());
                        break;
                    }
                }
            }
        }
        return result;
    }


}
