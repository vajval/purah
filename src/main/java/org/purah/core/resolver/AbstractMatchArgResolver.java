package org.purah.core.resolver;


import com.google.common.collect.Lists;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.InputToCheckerArg;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelFieldMatcher;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract class AbstractMatchArgResolver implements ArgResolver {


    /**
     * 根据 fieldMatcher 从instance中获取字段
     * 详情见单元测试
     */
    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        Object inputArg = inputToCheckerArg.argValue();

        if (inputArg == null) {
            return Collections.emptyMap();
        }
        if (!support(inputArg.getClass())) {
            throw new ArgResolverException("不支持的输入参数 argResolver:" + NameUtil.logClazzName(this) + "输入参数" + inputArg.getClass());
        }

        if (fieldMatcher instanceof MultilevelFieldMatcher) {
            MultilevelFieldMatcher multilevelFieldMatcher = (MultilevelFieldMatcher) fieldMatcher;
            return this.getMultiLevelMap(inputToCheckerArg, multilevelFieldMatcher);
        } else {
            return this.getThisLevelMatcherObjectMap(inputToCheckerArg, fieldMatcher);
        }
    }

    public abstract Map<String, InputToCheckerArg<?>> getThisLevelMatcherObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher);

    protected Map<String, InputToCheckerArg<?>> getMultiLevelMap(Object inputToCheckerArg, MultilevelFieldMatcher multilevelFieldMatcher) {
        return getMultiLevelMap(InputToCheckerArg.create(inputToCheckerArg),multilevelFieldMatcher);
    }

    /**
     * 获取多级 matcher  从 instance中获取多级对象，
     */
    protected Map<String, InputToCheckerArg<?>> getMultiLevelMap(InputToCheckerArg<?> inputToCheckerArg, MultilevelFieldMatcher multilevelFieldMatcher) {

        String levelSplitStr = multilevelFieldMatcher.levelSplitStr();
        Map<String, InputToCheckerArg<?>> result = new HashMap<>();
        Map<String, InputToCheckerArg<?>> fieldsObjectMap = this.getThisLevelMatcherObjectMap(inputToCheckerArg, multilevelFieldMatcher);
        for (Map.Entry<String, InputToCheckerArg<?>> entry : fieldsObjectMap.entrySet()) {
            String field = entry.getKey();
            InputToCheckerArg<?> innInputToCheckerArg = entry.getValue();

            FieldMatcher childFieldMatcher = multilevelFieldMatcher.childFieldMatcher(field);
            //不需要往底层看
            if (childFieldMatcher == null) {
                result.put(field, innInputToCheckerArg);
                continue;
            }

            //需要往底层看
            if (innInputToCheckerArg.argValue() != null && supportChildGet(innInputToCheckerArg.argValue().getClass())) {
                Map<String, InputToCheckerArg<?>> childMap = this.getMatchFieldObjectMap(innInputToCheckerArg, childFieldMatcher);
                for (Map.Entry<String, InputToCheckerArg<?>> childEntry : childMap.entrySet()) {
                    String childResultKey = childEntry.getKey();
                    InputToCheckerArg<?> childResultValue = childEntry.getValue();
                    childResultValue.addFieldPreByParent(field + levelSplitStr);
                    result.put(field + levelSplitStr + childResultKey, childResultValue);
                }
            }

        }
        return result;
    }


    protected boolean supportChildGet(Class<?> clazz) {
        ArrayList<Class<?>> unSupportGet = Lists.newArrayList(String.class,
                boolean.class, Boolean.class,
                int.class, Integer.class,
                short.class, Short.class,
                long.class, Long.class,
                byte.class, Byte.class, String.class, char.class, Character.class);
        return !unSupportGet.contains(clazz);

    }


}


