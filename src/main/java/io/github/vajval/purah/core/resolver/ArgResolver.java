package io.github.vajval.purah.core.resolver;


import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.checker.InputToCheckerArg;

import java.util.Map;


/**
 * 参数解析器
 * 输入对象与FieldMatcher 返回匹配到的字段的值及注解和Field信息
 */

public interface ArgResolver {
    /*
     *
     * Retrieve the values corresponding to all fields or nested fields matched by fieldMatcher,
     * and annotation
     */

    Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(InputToCheckerArg<?> inputArg, FieldMatcher fieldMatcher);


    /*
     * If the input parameter is null and lacks class information,
     * it will prevent the retrieval of fields and annotations through reflection.
     */

    default Map<String, InputToCheckerArg<?>> getMatchFieldObjectMap(Object inputArg, FieldMatcher fieldMatcher) {
        return getMatchFieldObjectMap(InputToCheckerArg.of(inputArg), fieldMatcher);
    }


}
