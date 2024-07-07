package org.purah.core.matcher.inft;


import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.multilevel.MultilevelMatchInfo;


/**
 *
 */


public interface MultilevelFieldMatcher extends IDefaultFieldMatcher {

    /**
     * 4种情况
     * 1 将 匹配到的值 加入到最终的返回结果
     * 2
     *
     * @param inputArg  被检查的对象
     * @param matchedField 匹配到的字段
     * @param childArg   子参数 匹配到的字段的值
     * @return 4种情况
     */


    MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg);


}
