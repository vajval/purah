package org.purah.core.resolver;


import com.google.common.collect.Lists;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.exception.ArgResolverException;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelFieldMatcher;
import org.purah.core.matcher.multilevel.MultilevelMatchInfo;
import org.springframework.util.CollectionUtils;


import java.util.*;


public abstract class AbstractMatchArgResolver implements ArgResolver {


    /**
     * 根据 fieldMatcher 从instance中获取字段
     * 详情见单元测试
     */
    @Override
    public Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher) {
        if (fieldMatcher == null) {
            throw new RuntimeException("不要传空的fieldMatcher");
        }

        Map<String, InputToCheckerArg<?>> result = new HashMap<>();
        putMatchFieldObjectMapToResult(inputToCheckerArg, fieldMatcher, result);
        return result;
    }


    public void putMatchFieldObjectMapToResult(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher, Map<String, InputToCheckerArg<?>> result) {

        if (inputToCheckerArg.isNull()) {
            Map<String, InputToCheckerArg<?>> thisLevelMatcherObjectMap = this.getThisLevelMatcherObjectMap(inputToCheckerArg, fieldMatcher);
            thisLevelMatcherObjectMap.forEach((a, b) -> result.put(b.fieldStr(), b));
        } else if (fieldMatcher instanceof MultilevelFieldMatcher) {
            MultilevelFieldMatcher multilevelFieldMatcher = (MultilevelFieldMatcher) fieldMatcher;
            this.putMultiLevelMapToResult(inputToCheckerArg, multilevelFieldMatcher, result);
        } else {
            Map<String, InputToCheckerArg<?>> thisLevelMatcherObjectMap = this.getThisLevelMatcherObjectMap(inputToCheckerArg, fieldMatcher);
            thisLevelMatcherObjectMap.forEach((a, b) -> result.put(b.fieldStr(), b));
        }

    }

    public abstract Map<String, InputToCheckerArg<?>> getThisLevelMatcherObjectMap(InputToCheckerArg<?> inputToCheckerArg, FieldMatcher fieldMatcher);


    /**
     * 获取多级 matcher  从 instance中获取多级对象，
     */
    protected void putMultiLevelMapToResult(InputToCheckerArg<?> inputToCheckerArg, MultilevelFieldMatcher multilevelFieldMatcher, Map<String, InputToCheckerArg<?>> result) {

        Map<String, InputToCheckerArg<?>> fieldsObjectMap = this.getThisLevelMatcherObjectMap(inputToCheckerArg, multilevelFieldMatcher);
        for (Map.Entry<String, InputToCheckerArg<?>> entry : fieldsObjectMap.entrySet()) {
            String field = entry.getKey();
            InputToCheckerArg<?> childArg = entry.getValue();
            MultilevelMatchInfo multilevelMatchInfo = multilevelFieldMatcher.childFieldMatcher(inputToCheckerArg, field, childArg);
            if (multilevelMatchInfo.isAddToFinal()) {
                InputToCheckerArg<?> resultArg = multilevelMatchInfo.getInputToCheckerArg();
                result.put(resultArg.fieldStr(), resultArg);
            }
            List<FieldMatcher> childFieldMatcherList = multilevelMatchInfo.getChildFieldMatcherList();

            //不需要往底层看
            if (CollectionUtils.isEmpty(childFieldMatcherList)) {
                continue;
            }

            for (FieldMatcher childFieldMatcher : childFieldMatcherList) {
                this.putMatchFieldObjectMapToResult(childArg, childFieldMatcher, result);
            }
        }
    }





}


