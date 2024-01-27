package com.purah.matcher.multilevel;


import com.purah.base.Name;
import com.purah.matcher.intf.FieldMatcher;
import com.purah.matcher.singleLevel.WildCardMatcher;

@Name("general")
public class GeneralMultilevelFieldMatcher extends AbstractMultilevelFieldMatcher {

    FieldMatcher firstLevelFieldMatcher;

    public GeneralMultilevelFieldMatcher(String matchStr) {
        super(matchStr);
        firstLevelFieldMatcher = new WildCardMatcher(firstLevelStr);
    }

    @Override
    public boolean match(String field) {
        return firstLevelFieldMatcher.match(field);
    }

    @Override
    public FieldMatcher childFieldMatcherByChildStr(String childMatchStr) {
        if (childMatchStr.contains(this.levelSplitStr())) {
            return new GeneralMultilevelFieldMatcher(childMatchStr);
        }
        return new WildCardMatcher(childMatchStr);

    }

    @Override
    public String cacheKey() {
        return "general[" + this.matchStr + "]";
    }
}
