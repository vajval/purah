package com.purah.resolver;


import com.google.common.collect.Lists;
import com.purah.base.NameUtil;
import com.purah.checker.CheckInstance;
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
    public Map<String, CheckInstance> getMatchFieldObjectMap(INSTANCE instance, FieldMatcher fieldMatcher) {
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
    protected Map<String, CheckInstance> getMultiLevelMap(INSTANCE instance, MultilevelFieldMatcher multilevelFieldMatcher) {

        Set<String> matchFieldList = this.matchFieldList(instance, multilevelFieldMatcher);
        String levelSplitStr = multilevelFieldMatcher.levelSplitStr();
        Map<String, CheckInstance> result = new HashMap<>();
        Map<String, CheckInstance> fieldsObjectMap = this.getFieldsObjectMap(instance, matchFieldList);


        for (Map.Entry<String, CheckInstance> entry : fieldsObjectMap.entrySet()) {
            String field = entry.getKey();
            CheckInstance innCheckInstance = entry.getValue();

            FieldMatcher childFieldMatcher = multilevelFieldMatcher.childFieldMatcher(field);

            //不需要往底层看
            if (childFieldMatcher == null) {
                result.put(field, innCheckInstance);
                continue;
            }


            //不需要往底层看
            if (innCheckInstance != null && supportChildGet(innCheckInstance.instance().getClass())) {
                Map<String, CheckInstance> childMap = this.getChildMap(innCheckInstance.instance(), childFieldMatcher);
                for (Map.Entry<String, CheckInstance> childEntry : childMap.entrySet()) {
                    String childResultKey = childEntry.getKey();
                    CheckInstance childResultValue = childEntry.getValue();
                    result.put(field + levelSplitStr + childResultKey, childResultValue);
                }
            }

        }

        return result;
    }

    protected Map<String, CheckInstance> getChildMap(Object innObject, FieldMatcher childFieldMatcher) {
        return this.getMatchFieldObjectMap((INSTANCE) innObject, childFieldMatcher);
    }


    protected Map<String, CheckInstance> getSingleLevelMap(INSTANCE instance, FieldMatcher fieldMatcher) {
        Set<String> matchFieldList = this.matchFieldList(instance, fieldMatcher);
        return getFieldsObjectMap(instance, matchFieldList);
    }

    public abstract Map<String, CheckInstance> getFieldsObjectMap(INSTANCE instance, Set<String> matchFieldList);

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


