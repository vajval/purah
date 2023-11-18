package com.purah.matcher.multilevel;


import com.purah.matcher.BaseStringMatcher;
import com.purah.matcher.intf.FieldMatcher;

public abstract class AbstractMultilevelFieldMatcher extends BaseStringMatcher implements MultilevelFieldMatcher {

    String firstLevelStr;

    public AbstractMultilevelFieldMatcher(String matchStr) {
        super(matchStr);
        int index = matchStr.indexOf(this.levelSplitStr());
        if (index != -1) firstLevelStr = matchStr.substring(0, index);
        else firstLevelStr = matchStr;

    }


    @Override
    public abstract boolean match(String field);


    public abstract FieldMatcher childFieldMatcherByChildStr(String childMatchStr);


    /**
     *
     * @param matchedField
     * @return
     */



    @Override
    public FieldMatcher childFieldMatcher(String matchedField) {
        String levelSplitStr = levelSplitStr();
        int index = matchStr.indexOf(levelSplitStr);
        if (index == -1) {
            return null;
        }
        String childMatchStr = matchStr.substring(index+1);
        return  childFieldMatcherByChildStr(childMatchStr);

    }
}
