package org.purah.springboot.custom.matcher;

import com.google.common.collect.Sets;
import com.purah.base.FieldGetMethodUtil;
import com.purah.base.Name;
import com.purah.matcher.clazz.AbstractInstanceFieldMatcher;
import org.purah.springboot.ann.EnableOnPurahContext;
import org.purah.springboot.custom.ann.NotEmpty;
import org.purah.springboot.custom.ann.Range;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableOnPurahContext
@Name("custom_ann")
public class CustomAnnMatcher extends AbstractInstanceFieldMatcher {
    Set<Class<? extends Annotation>> customAnnList = Sets.newHashSet(NotEmpty.class, Range.class);


    public CustomAnnMatcher(String matchStr) {

        super(matchStr);
    }

    @Override
    public List<String> getFieldsByClass(Class<?> clazz) {


        fieldGetMethodUtil = new FieldGetMethodUtil();
        List<String> result = new ArrayList<>();
        Map<Field, Method> fieldMethodMap = fieldGetMethodUtil.fieldGetMethodMap(clazz);
        for (Map.Entry<Field, Method> entry : fieldMethodMap.entrySet()) {
            Field field = entry.getKey();
            for (Annotation declaredAnnotation : field.getDeclaredAnnotations()) {

                if (customAnnList.contains(declaredAnnotation.annotationType())) {
                    result.add(field.getName());
                    break;
                }

            }
        }
        return result;
    }
}
