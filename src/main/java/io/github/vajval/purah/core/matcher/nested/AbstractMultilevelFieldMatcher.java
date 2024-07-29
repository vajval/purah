package io.github.vajval.purah.core.matcher.nested;


import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.inft.IDefaultFieldMatcher;
import io.github.vajval.purah.core.matcher.inft.ListIndexMatcher;
import io.github.vajval.purah.core.matcher.WrapListFieldMatcher;
import io.github.vajval.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * 输入 fullMatchStr 分成firstLevelStr和childStr   child.name-> child     name
 * listIndexStr对list支持,是数字就填充到 listIndex ,不是就自己实现解析
 * @param <T>
 */

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

    protected static final int NO_LIST_INDEX = Integer.MIN_VALUE;

    protected static final int OTHER_LIST_MATCH = Integer.MIN_VALUE + 1;

    public AbstractMultilevelFieldMatcher(String matchStr) {
        super(matchStr);
        if (wrapChildList != null) {
            return;
        }
        initStr();
        initListIndex();

    }


    /*
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

    /*
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
            return multilevelMatchInfoByWrapChild(inputArg, matchedField, childArg);
        }
        if (childStr == null) {
            return NestedMatchInfo.needCollected();
        }
        return NestedMatchInfo.justNested(wrapChildMatcher(childStr));
    }

    protected NestedMatchInfo multilevelMatchInfoByWrapChild(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        boolean addToFinal = false;
        List<FieldMatcher> fieldMatchers = new ArrayList<>();
        for (T optionMatcher : wrapChildList) {
            if (optionMatcher.match(matchedField, inputArg.argValue())) {
                NestedMatchInfo nestedMatchInfo = optionMatcher.nestedFieldMatcher(inputArg, matchedField, childArg);
                addToFinal = addToFinal || nestedMatchInfo.isNeedCollected();
                if (nestedMatchInfo.getNestedFieldMatcherList() != null) {
                    fieldMatchers.addAll(nestedMatchInfo.getNestedFieldMatcherList());
                }
            }
        }
        if (addToFinal) {
            return NestedMatchInfo.needCollectedAndMatchNested(fieldMatchers);
        }
        return NestedMatchInfo.justNested(fieldMatchers);
    }

    protected abstract IDefaultFieldMatcher initFirstLevelFieldMatcher(String str);

    protected abstract T wrapChildMatcher(String matchStr);

}
