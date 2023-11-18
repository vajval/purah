package com.purah.resolver;


import com.google.common.collect.Lists;
import com.purah.base.NameUtil;
import com.purah.exception.ArgResolverException;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.matcher.multilevel.MultilevelFieldMatcher;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @param <INSTANCE>
 */
public abstract class AbstractMatchArgResolver<INSTANCE> extends BaseArgResolver<INSTANCE> {


    /**
     * 根据 fieldMatcher 从instance中获取字段
     * 详情见单元测试
     */
    @Override
    public Map<String, Object> getMatchFieldObjectMap(INSTANCE instance, FieldMatcher fieldMatcher) {
        this.baseCheck(instance);
        if (fieldMatcher instanceof MultilevelFieldMatcher multilevelFieldMatcher) {
            return this.getMultiLevelMap(instance, multilevelFieldMatcher);
        } else {
            return this.getSingleLevelMap(instance, fieldMatcher);
        }
    }


    /**
     * 获取多级 matcher  从 instance中获取多级对象，
     */
    protected Map<String, Object> getMultiLevelMap(INSTANCE instance, MultilevelFieldMatcher multilevelFieldMatcher) {

        Set<String> matchFieldList = this.matchFieldList(instance, multilevelFieldMatcher);
        String levelSplitStr = multilevelFieldMatcher.levelSplitStr();
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> fieldsObjectMap = this.getFieldsObjectMap(instance, matchFieldList);


        for (Map.Entry<String, Object> entry : fieldsObjectMap.entrySet()) {
            String field = entry.getKey();
            Object innObject = entry.getValue();

            FieldMatcher childFieldMatcher = multilevelFieldMatcher.childFieldMatcher(field);

            //不需要往底层看
            if (childFieldMatcher == null) {
                result.put(field, innObject);
                continue;
            }

            //不需要往底层看
            if (innObject != null && supportChildGet(innObject.getClass())) {
                Map<String, Object> childMap = this.getChildMap(innObject, childFieldMatcher);
                for (Map.Entry<String, Object> childEntry : childMap.entrySet()) {
                    String childResultKey = childEntry.getKey();
                    Object childResultValue = childEntry.getValue();
                    result.put(field + levelSplitStr + childResultKey, childResultValue);
                }
            }

        }

        return result;
    }

    protected Map<String, Object> getChildMap(Object innObject, FieldMatcher childFieldMatcher) {
        return this.getMatchFieldObjectMap((INSTANCE) innObject, childFieldMatcher);
    }


    protected Map<String, Object> getSingleLevelMap(INSTANCE instance, FieldMatcher fieldMatcher) {
        Set<String> matchFieldList = this.matchFieldList(instance, fieldMatcher);
        return getFieldsObjectMap(instance, matchFieldList);
    }

    public abstract Map<String, Object> getFieldsObjectMap(INSTANCE instance, Set<String> matchFieldList);

    protected Set<String> matchFieldList(INSTANCE instance, FieldMatcher fieldMatcher) {
        return fieldMatcher.matchFields(fields(instance));
    }

    protected abstract Set<String> fields(INSTANCE instance);

    protected boolean supportChildGet(Class<?> clazz) {
        ArrayList<Class<?>> unSupportGet = Lists.newArrayList(String.class,
                boolean.class, Boolean.class,
                int.class, Integer.class,
                short.class, Short.class,
                long.class, Long.class,
                byte.class, Byte.class, String.class, char.class, Character.class);
        return !unSupportGet.contains(clazz);

    }

    protected void baseCheck(INSTANCE instance) {
        if (instance == null) {
            throw new ArgResolverException("不支持 解析null:" + NameUtil.useName(this));
        }
        if (!support(instance.getClass())) {
            throw new ArgResolverException("不支持的输入参数 argResolver:" + NameUtil.useName(this) + "输入参数" + instance.getClass());
        }
    }
}


