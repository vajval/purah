package org.purah.core.resolver;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.checker.ITCArgNullType;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * result {a.b.c=arg("a.b.c",value,[@Ann1("v1"),@Ann2("2v")],field_info)}
 * cache  (inputArg)-> new arg("a.b.c",invokeGet(inputArg,"a.b.c"),[@Ann1("v1"),@Ann2("2v")],field_info)
 * invoke cache to build result
 */

public class FieldMatcherResultReflectInvokeCache {

    protected static final Logger logger = LogManager.getLogger(ClassReflectCache.class);

    protected Class<?> inputArgClass;
    protected FieldMatcher cachedFieldMatcher;
    protected Map<String, Function<Object, InputToCheckerArg<?>>> inputArgToResultInovkeByFieldNameCacheMap = new ConcurrentHashMap<>();
    protected Map<String, Function<Object, InputToCheckerArg<?>>> inputArgToResultNullInovkeMap = new ConcurrentHashMap<>();
    protected int resultSize;

    public FieldMatcherResultReflectInvokeCache(Class<?> inputArgClass, FieldMatcher cachedFieldMatcher, Map<String, InputToCheckerArg<?>> result) {
        this.inputArgClass = inputArgClass;
        this.cachedFieldMatcher = cachedFieldMatcher;
        this.resultSize = result.size();
        for (Map.Entry<String, InputToCheckerArg<?>> entry : result.entrySet()) {
            InputToCheckerArg<?> childArg = entry.getValue();
            String fieldName = entry.getKey();
            if (childArg.isNull()) {
                if (childArg.nullType() == ITCArgNullType.no_field_no_getter) {
                    inputArgToResultNullInovkeMap.put(fieldName, i -> InputToCheckerArg.createNullChildWithFieldConfig(childArg.fieldStr(), childArg.field(), childArg.annListOnField(), ITCArgNullType.no_field_no_getter));
                    continue;
                }
                if (childArg.nullType() == ITCArgNullType.have_field_no_getter) {
                    inputArgToResultNullInovkeMap.put(fieldName, i -> {
                        logger.warn("set null value because not getter function for class {}, field: {}", inputArgClass, childArg.field().getName());
                        return InputToCheckerArg.createNullChildWithFieldConfig(childArg.fieldStr(), childArg.field(), childArg.annListOnField(), ITCArgNullType.have_field_no_getter);
                    });
                    continue;
                }
            }
            inputArgToResultInovkeByFieldNameCacheMap.put(fieldName, i -> InputToCheckerArg.createChildWithFieldConfig(i, childArg.fieldStr(), childArg.field(), childArg.annListOnField()));
        }
    }


    public Map<String, InputToCheckerArg<?>> invokeResultByCache(Object inputArg) {
        Map<String, InputToCheckerArg<?>> result = Maps.newHashMapWithExpectedSize(resultSize);
        for (Map.Entry<String, Function<Object, InputToCheckerArg<?>>> entry : inputArgToResultInovkeByFieldNameCacheMap.entrySet()) {
            String field = entry.getKey();
            Function<Object, InputToCheckerArg<?>> invoke = entry.getValue();
            Object ignoreNull = ClassReflectCache.getIgnoreNull(inputArg, field);
            result.put(field, invoke.apply(ignoreNull));
        }

        for (Map.Entry<String, Function<Object, InputToCheckerArg<?>>> entry : inputArgToResultNullInovkeMap.entrySet()) {
            String field = entry.getKey();
            Function<Object, InputToCheckerArg<?>> invoke = entry.getValue();
            result.put(field, invoke.apply(null));
        }


        return result;
    }



    /**
     * If this value is null, it's uncertain whether the object's class is People or SuperPeople,
     * potentially leading to errors in retrieving annotations.
     * If all fields in the People class are final, this issue doesn't need consideration.
     * class People{
     *
     * @Test(id) String id;
     * @Test(child) People child;   //
     * }
     * <p>
     *
     * <p>
     * class SuperPeople extend People{
     * @Test(superId) String id;   // ann change
     * }
     */


    public static boolean noExtendEnabledFields(Class<?> clazz, Set<String> fields, Set<Class<?>> clazzSet) {
        Map<String, PropertyDescriptor> collect = Stream.of(PropertyUtils.getPropertyDescriptors(clazz)).collect(Collectors.toMap(FeatureDescriptor::getName, i -> i));
        Map<String, Set<String>> fieldMap = fields.stream().collect(Collectors.groupingBy(field -> {
            if (field.contains(".")) {
                return field.substring(0, field.indexOf("."));
            }
            return field;
        }, Collectors.toSet()));
        for (Map.Entry<String, Set<String>> entry : fieldMap.entrySet()) {
            PropertyDescriptor propertyDescriptor = collect.get(entry.getKey());
            if (propertyDescriptor == null) {
                continue;
            }
            Class<?> propertyType = propertyDescriptor.getPropertyType();

            int modifiers = propertyType.getModifiers();
            if (!Modifier.isFinal(modifiers)) {
                return false;
            }
            if (clazzSet.contains(propertyType)) continue;
            clazzSet.add(propertyType);
            if (!noExtendEnabledFields(propertyType, fields, clazzSet)) {
                return false;
            }
        }
        return true;
    }

}