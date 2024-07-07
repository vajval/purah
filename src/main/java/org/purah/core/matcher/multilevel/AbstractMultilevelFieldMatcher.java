package org.purah.core.matcher.multilevel;


import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.inft.FieldMatcher;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;
import org.purah.core.matcher.inft.ListIndexMatcher;
import org.purah.core.matcher.inft.ListableFieldMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultilevelFieldMatcher<T extends MultilevelFieldMatcher> extends ListableFieldMatcher<T> implements MultilevelFieldMatcher, ListIndexMatcher {

    protected String firstLevelStr;
    protected String childStr;
    protected IDefaultFieldMatcher firstLevelFieldMatcher;
    protected String fullMatchStr;

    protected Integer listIndex = -1;

    protected String listIndexStr;

    protected static int NO_LIST_INDEX = 1;


    public AbstractMultilevelFieldMatcher(String matchStr) {
        super(matchStr);
        if (wrapChildList != null) {
            return;
        }
        initStr();
        initListIndex();

    }


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


    protected void initListIndex() {
        int index = firstLevelStr.indexOf("#");
        if (index == -1) {
            listIndex = -1;
        } else {
            listIndexStr = firstLevelStr.substring(index + 1);
            try {
                listIndex = Integer.parseInt(listIndexStr);
            } catch (Exception e) {
                listIndex = -2;
            }
        }
    }


    @Override
    public boolean matchBySelf(String field, Object belongInstance) {
        return firstLevelFieldMatcher.match(field, belongInstance);
    }

    @Override
    public MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        if (wrapChildList != null) {
            return multilevelMatchInfoByChild(inputArg, matchedField, childArg);
        }
        if (childStr == null) {
            return MultilevelMatchInfo.addToFinal(childArg);
        }
        return MultilevelMatchInfo.justChild(wrapChildMatcher(childStr));
    }

    protected MultilevelMatchInfo multilevelMatchInfoByChild(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        boolean addToFinal = false;
        List<FieldMatcher> fieldMatchers = new ArrayList<>();
        for (T optionMatcher : wrapChildList) {
            if (optionMatcher.match(matchedField, inputArg.argValue())) {
                MultilevelMatchInfo multilevelMatchInfo = optionMatcher.childFieldMatcher(inputArg, matchedField, childArg);
                addToFinal = addToFinal || multilevelMatchInfo.isAddToFinal();
                if (multilevelMatchInfo.getChildFieldMatcherList() != null) {
                    fieldMatchers.addAll(multilevelMatchInfo.getChildFieldMatcherList());
                }
            }
        }
        if (addToFinal) {
            return MultilevelMatchInfo.addToFinalAndChildMatcher(fieldMatchers, childArg);
        }
        return MultilevelMatchInfo.justChild(fieldMatchers);
    }

    protected abstract IDefaultFieldMatcher initFirstLevelFieldMatcher(String str);

    protected abstract T wrapChildMatcher(String matchStr);

}
