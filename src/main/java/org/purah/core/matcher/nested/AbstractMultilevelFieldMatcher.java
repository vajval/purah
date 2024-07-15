package org.purah.core.matcher.nested;


import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;
import org.purah.core.matcher.inft.ListIndexMatcher;
import org.purah.core.matcher.WrapListFieldMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultilevelFieldMatcher<T extends MultilevelFieldMatcher> extends WrapListFieldMatcher<T> implements MultilevelFieldMatcher, ListIndexMatcher {

    // The matching string at the current level, for example, in `child.name`,
    // `firstLevelStr` is `child`, and `childStr` is `name`.

    protected String firstLevelStr;
    protected String childStr;
    //  matcher build by firstLevelStr
    protected IDefaultFieldMatcher firstLevelFieldMatcher;
    //  Constructor parameters are not split into the original string before firstLevelStr and childStr.
    protected String fullMatchStr;
    // Support for lists, set the index to be matched. if child#5  listIndex=5
    protected Integer listIndex = NO_LIST_INDEX;
    // Support for lists, set the index str . if child#5  listIndexStr=#5.   if child#*  listIndexStr=#*
    protected String listIndexStr;

    protected static int NO_LIST_INDEX = Integer.MIN_VALUE;

    protected static int OTHER_LIST_MATCH = Integer.MIN_VALUE + 1;

    public AbstractMultilevelFieldMatcher(String matchStr) {
        super(matchStr);
        if (wrapChildList != null) {
            return;
        }
        initStr();
        initListIndex();

    }

    /**
     * child#6.child#2.name ->
     * fullMatchStr = child#6.child#2.name
     * firstLevelStr = child
     * childStr = #6.child#2.name
     */

    protected void initStr() {
        fullMatchStr = matchStr;
        int index = matchStr.indexOf(".");
        firstLevelStr = matchStr;
        childStr = "";
        if (index != -1) {
            firstLevelStr = matchStr.substring(0, index);
            childStr = matchStr.substring(index + 1);
        }
        index = firstLevelStr.indexOf("#");
        if (index != -1 && index != 0) {
            childStr = firstLevelStr.substring(index) + "." + childStr;
            firstLevelStr = matchStr.substring(0, index);
        }
        if (!StringUtils.hasText(childStr)) {
            childStr = null;
        }
        firstLevelFieldMatcher = initFirstLevelFieldMatcher(firstLevelStr);
    }

    /**
     * child#6 -> listIndex=6
     * child#* -> listIndex=-2
     * child ->   listIndex=-1
     */

    protected void initListIndex() {
        int index = firstLevelStr.indexOf("#");
        if (index == -1) {
            listIndex = -1;
        } else {
            listIndexStr = firstLevelStr.substring(index + 1);
            try {
                listIndex = Integer.parseInt(listIndexStr);
            } catch (Exception e) {
                listIndex = OTHER_LIST_MATCH;
            }
        }
    }


    @Override
    public boolean matchBySelf(String field, Object belongInstance) {
        return firstLevelFieldMatcher.match(field, belongInstance);
    }

    @Override
    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        if (wrapChildList != null) {
            return multilevelMatchInfoByChild(inputArg, matchedField, childArg);
        }
        if (childStr == null) {
            return NestedMatchInfo.addToResult();
        }
        return NestedMatchInfo.justNested(wrapChildMatcher(childStr));
    }

    protected NestedMatchInfo multilevelMatchInfoByChild(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        boolean addToFinal = false;
        List<FieldMatcher> fieldMatchers = new ArrayList<>();
        for (T optionMatcher : wrapChildList) {
            if (optionMatcher.match(matchedField, inputArg.argValue())) {
                NestedMatchInfo nestedMatchInfo = optionMatcher.nestedFieldMatcher(inputArg, matchedField, childArg);
                addToFinal = addToFinal || nestedMatchInfo.isAddToResult();
                if (nestedMatchInfo.getNestedFieldMatcherList() != null) {
                    fieldMatchers.addAll(nestedMatchInfo.getNestedFieldMatcherList());
                }
            }
        }
        if (addToFinal) {
            return NestedMatchInfo.addToResultAndMatchNested(fieldMatchers);
        }
        return NestedMatchInfo.justNested(fieldMatchers);
    }

    protected abstract IDefaultFieldMatcher initFirstLevelFieldMatcher(String str);

    protected abstract T wrapChildMatcher(String matchStr);

}
