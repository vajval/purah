package org.purah.core.matcher.multilevel;


import org.purah.core.base.Name;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.WildCardMatcher;
import org.springframework.util.StringUtils;

@Name("general")
public class GeneralFieldMatcher extends AbstractMultilevelFieldMatcher {

    FieldMatcher firstLevelFieldMatcher;
    String firstLevelStr;
    String childStr;


    public GeneralFieldMatcher(String matchStr) {
        super(matchStr);
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
        firstLevelFieldMatcher = new WildCardMatcher(firstLevelStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        return firstLevelFieldMatcher.match(field);
    }


    @Override
    public MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject) {
        if (childStr == null) {
            return MultilevelMatchInfo.addToFinal();
        }
        if (childStr.contains(".") || childStr.contains("#")) {
            return MultilevelMatchInfo.addToFinalAndChildMatcher(new GeneralFieldMatcher(childStr));
        }
        return MultilevelMatchInfo.justChild(new WildCardMatcher(childStr));
    }


    @Override
    public String toString() {
        return "GeneralFieldMatcher{" +
                "firstLevelFieldMatcher=" + firstLevelFieldMatcher +
                ", firstLevelStr='" + firstLevelStr + '\'' +
                ", childStr='" + childStr + '\'' +
                '}';
    }
}
