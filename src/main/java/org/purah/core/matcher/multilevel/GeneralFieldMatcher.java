package org.purah.core.matcher.multilevel;


import org.purah.core.base.Name;
import org.purah.core.matcher.intf.FieldMatcher;
import org.purah.core.matcher.singleLevel.WildCardMatcher;

@Name("general")
public class GeneralFieldMatcher extends AbstractMultilevelFieldMatcher {

    FieldMatcher firstLevelFieldMatcher;
    String firstLevelStr;
    String childStr;

    public GeneralFieldMatcher(String matchStr) {
        super(matchStr);
        int index = matchStr.indexOf(this.levelSplitStr());
        if (matchStr.startsWith("[")) {
            int num = 0;
            index = 0;
            for (; index < matchStr.length(); index++) {
                if (matchStr.charAt(index) == '[') {
                    num++;
                } else if (matchStr.charAt(index) == ']') {
                    num--;
                }
                if (num == 0) break;
            }
            index++;
        }
        if (index != -1) {
            firstLevelStr = matchStr.substring(0, index);
        } else {
            firstLevelStr = matchStr;
        }
        System.out.println(firstLevelStr);
        if (this.matchStr.equals(firstLevelStr)) {
            childStr = null;
        } else {
            childStr = this.matchStr.substring(this.firstLevelStr.length() + 1);
        }
        firstLevelFieldMatcher = new WildCardMatcher(firstLevelStr);


    }


    @Override
    public boolean match(String field) {
        return firstLevelFieldMatcher.match(field);
    }

    @Override
    public FieldMatcher childFieldMatcher(String matchedField) {


        if (childStr == null) {
            return null;
        }

        if (childStr.contains(this.levelSplitStr())) {
            return new GeneralFieldMatcher(childStr);
        }
        return new WildCardMatcher(childStr);
    }


}
