package org.purah.core.matcher.multilevel;


import org.purah.core.base.Name;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

@Name("general")
public class GeneralFieldMatcher extends AbstractMultilevelFieldMatcher {

    FieldMatcher firstLevelFieldMatcher;

    public GeneralFieldMatcher(String matchStr) {
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
            return new GeneralFieldMatcher(childMatchStr);
        }
        return new WildCardMatcher(childMatchStr);

    }


}
