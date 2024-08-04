package io.github.vajval.purah.core.matcher.nested;

import org.springframework.util.StringUtils;

public class MatchStrS {
    String firstLevelStr;
    String childStr;
    String fullMatchStr;
    public MatchStrS(String matchStr) {
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
        this.initListIndex();
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
                listIndex = OTHER_LIST_MATCH;
            }
        }
    }
    protected Integer listIndex = NO_LIST_INDEX;
    // Support for lists, set the index str . if child#5  listIndexStr=#5.   if child#*  listIndexStr=#*
    protected String listIndexStr;

    protected static final int NO_LIST_INDEX = Integer.MIN_VALUE;

    protected static final int OTHER_LIST_MATCH = Integer.MIN_VALUE + 1;


}
