package org.purah.core.resolver;


import com.google.common.collect.Lists;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.InputCheckArg;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.matcher.intf.FieldMatcher;
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
    public Map<String, InputCheckArg<?>> getMatchFieldObjectMap(Object inputArg, FieldMatcher fieldMatcher) {
        if (inputArg == null) {
            return Collections.emptyMap();
        }
        if (!support(inputArg.getClass())) {
            throw new ArgResolverException("不支持的输入参数 argResolver:" + NameUtil.logClazzName(this) + "输入参数" + inputArg.getClass());
        }

        if (fieldMatcher instanceof MultilevelFieldMatcher) {
            MultilevelFieldMatcher multilevelFieldMatcher = (MultilevelFieldMatcher) fieldMatcher;
            return this.getMultiLevelMap(inputArg, multilevelFieldMatcher);
        } else {
            return this.getThisLevelMatcherObjectMap(inputArg, fieldMatcher);
        }
    }

    public abstract Map<String, InputCheckArg<?>> getThisLevelMatcherObjectMap(Object inputArg, FieldMatcher fieldMatcher);

    /**
     * 获取多级 matcher  从 instance中获取多级对象，
     */
    protected Map<String, InputCheckArg<?>> getMultiLevelMap(Object inputArg, MultilevelFieldMatcher multilevelFieldMatcher) {

        String levelSplitStr = multilevelFieldMatcher.levelSplitStr();
        Map<String, InputCheckArg<?>> result = new HashMap<>();
        Map<String, InputCheckArg<?>> fieldsObjectMap = this.getThisLevelMatcherObjectMap(inputArg, multilevelFieldMatcher);
        System.out.println(fieldsObjectMap);

        for (Map.Entry<String, InputCheckArg<?>> entry : fieldsObjectMap.entrySet()) {
            String field = entry.getKey();
            InputCheckArg<?> innInputCheckArg = entry.getValue();

            FieldMatcher childFieldMatcher = multilevelFieldMatcher.childFieldMatcher(field);
            System.out.println(childFieldMatcher);
            //不需要往底层看
            if (childFieldMatcher == null) {
                result.put(field, innInputCheckArg);
                continue;
            }

            //需要往底层看
            if (innInputCheckArg.inputArg() != null && supportChildGet(innInputCheckArg.inputArg().getClass())) {

                Map<String, InputCheckArg<?>> childMap = this.getMatchFieldObjectMap(innInputCheckArg.inputArg(), childFieldMatcher);


                for (Map.Entry<String, InputCheckArg<?>> childEntry : childMap.entrySet()) {
                    String childResultKey = childEntry.getKey();
                    InputCheckArg<?> childResultValue = childEntry.getValue();
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


