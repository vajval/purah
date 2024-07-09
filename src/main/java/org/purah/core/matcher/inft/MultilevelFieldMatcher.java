package org.purah.core.matcher.inft;


import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.multilevel.MultilevelMatchInfo;


public interface MultilevelFieldMatcher extends IDefaultFieldMatcher {

    /**
     * 4 scenarios:
     * 1. Add the matched value to the final result.
     * 2. Add the matched value to the final result and apply some child field matcher(s) to match values within the field.
     * 3. Do not add the matched value to the final result, only apply some child field matcher(s) to match values within the field.
     * 4. Ignore the matched value entirely.
     *
     * @param inputArg     The object being inspected.
     * @param matchedField The field that was matched.
     * @param childArg     The value of the matched field.
     * @return One of the 4 scenarios.
     */



    MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg);


}
