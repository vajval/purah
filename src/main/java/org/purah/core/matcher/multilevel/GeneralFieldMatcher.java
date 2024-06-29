package org.purah.core.matcher.multilevel;


import com.google.common.collect.Maps;
import org.purah.core.base.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.util.*;

@Name("general")
public class GeneralFieldMatcher extends AbstractMultilevelFieldMatcher {


    boolean isOption;
    boolean childIsWildCard;
    boolean childIsMultiLevel;

    public GeneralFieldMatcher(String matchStr) {
        super(matchStr);
        isOption = !isWildCardMatcher(childStr) && !isWildCardMatcher(firstLevelStr);
        if (childStr == null) {
            childIsWildCard = false;
            childIsMultiLevel = false;
        } else {
            childIsWildCard = isWildCardMatcher(childStr);
            childIsMultiLevel = childStr.contains(".");
        }

    }

    public static boolean isWildCardMatcher(String s) {
        if (!StringUtils.hasText(s)) {
            return false;
        }
        return s.contains("*") || s.contains("+") || s.contains("[") || s.contains("{") || s.contains("?") || s.contains("^") || s.contains("!");
    }

    @Override
    protected FieldMatcher initFirstLevelFieldMatcher(String str) {
        return new WildCardMatcher(str);
    }

    @Override
    protected MultilevelFieldMatcher wrapChildMatcher(String matchStr) {
        boolean isWildCardMatcher = isWildCardMatcher(matchStr);
        if (isWildCardMatcher) {
            return new GeneralFieldMatcher(matchStr);
        }
        return new FixedMatcher(matchStr);
    }


    @Override
    public MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {

        if (wrapChildList != null) {
            return multilevelMatchInfoByChild(inputArg, matchedField, childArg);
        }
        if (childStr == null) {
            return MultilevelMatchInfo.addToFinal(childArg);
        }
        if (isOption) {
            return MultilevelMatchInfo.justChild(new FixedMatcher(childStr));
        }
        FieldMatcher fieldMatcher;

        if (childIsWildCard) {
            if (childIsMultiLevel) {
                fieldMatcher = new GeneralFieldMatcher(childStr);
            } else {
                fieldMatcher = new WildCardMatcher(childStr);
            }
        } else {
            fieldMatcher = new NormalMultiLevelMatcher(childStr);
        }
        return MultilevelMatchInfo.justChild(fieldMatcher);
    }


    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        if (!firstLevelStr.contains("#")) {
            return Collections.emptyMap();
        }

        String substring = firstLevelStr.substring(firstLevelStr.indexOf("#") + 1);
        if (substring.equals("*")) {
            Map<String, Object> result = Maps.newHashMapWithExpectedSize(objectList.size());
            for (int index = 0; index < objectList.size(); index++) {
                String fieldStr = "#" + index;
                result.put(fieldStr, objectList.get(index));
            }
            return result;

        }
        try {
            int i = Integer.parseInt(substring);
            if (i < objectList.size()) {
                return Collections.singletonMap("#" + i, objectList.get(i));
            }
            return Collections.emptyMap();
        } catch (Exception e) {
            Map<String, Object> result = Maps.newHashMapWithExpectedSize(objectList.size());
            for (int index = 0; index < objectList.size(); index++) {
                String fieldStr = "#" + index;
                if (this.match(fieldStr, objectList.get(index))) {
                    result.put(fieldStr, objectList.get(index));
                }
            }
            return result;
        }
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
