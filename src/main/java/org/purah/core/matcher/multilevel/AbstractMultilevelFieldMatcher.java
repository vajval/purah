package org.purah.core.matcher.multilevel;


import com.google.common.base.Splitter;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.ListIndexMatcher;
import org.purah.core.matcher.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractMultilevelFieldMatcher extends BaseStringMatcher implements MultilevelFieldMatcher, ListIndexMatcher {

    protected String firstLevelStr;
    protected String childStr;
    protected FieldMatcher firstLevelFieldMatcher;
    protected String fullMatchStr;

    List<MultilevelFieldMatcher> wrapChildList;

    public AbstractMultilevelFieldMatcher(String matchStr) {
        super(matchStr);
        if (matchStr.contains("|")) {
            wrapChildList = Splitter.on("|").splitToList(matchStr).stream().map(this::wrapChildMatcher).collect(Collectors.toList());
            return;
        }
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

    @Override
    public boolean match(String field, Object belongInstance) {

        if (wrapChildList != null) {
            for (FieldMatcher matcher : wrapChildList) {
                if (matcher.match(field, belongInstance)) {
                    return true;
                }
            }
            return false;
        }
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
        for (MultilevelFieldMatcher optionMatcher : wrapChildList) {
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

    protected abstract FieldMatcher initFirstLevelFieldMatcher(String str);

    protected abstract MultilevelFieldMatcher wrapChildMatcher(String matchStr);

}
