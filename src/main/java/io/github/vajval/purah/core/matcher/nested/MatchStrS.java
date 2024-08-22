package io.github.vajval.purah.core.matcher.nested;

import org.springframework.util.StringUtils;

public class MatchStrS {
    String firstLevelStr;
    String childStr;
    final String fullMatchStr;

    /*
     * child#6.child#2.name ->
     * fullMatchStr = child#6.child#2.name
     * firstLevelStr = child
     * childStr = #6.child#2.name
     */
    public MatchStrS(String matchStr) {
        matchStr = matchStr.trim();
        fullMatchStr = matchStr;
        int index = matchStr.indexOf(".");
        boolean contains = matchStr.contains(".");
        firstLevelStr = matchStr;
        childStr = "";
        if (index != -1) {
            firstLevelStr = matchStr.substring(0, index);
            childStr = matchStr.substring(index + 1);
        }
        index = firstLevelStr.indexOf("#");
        if (index != -1 && index != 0) {
            if (contains) {
                childStr = firstLevelStr.substring(index) + "." + childStr;
            } else {
                childStr = firstLevelStr.substring(index);
            }
            firstLevelStr = matchStr.substring(0, index);
        }
        if (!StringUtils.hasText(childStr)) {
            childStr = null;
        }
        this.initListIndex();
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

    protected Integer listIndex = NO_LIST_INDEX;
    // Support for lists, set the index str . if child#5  listIndexStr=#5.   if child#*  listIndexStr=#*
    protected String listIndexStr;

    protected static final int NO_LIST_INDEX = Integer.MIN_VALUE;

    protected static final int OTHER_LIST_MATCH = Integer.MIN_VALUE + 1;


}
