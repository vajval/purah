package org.purah.core.matcher.multilevel;


import com.google.common.collect.Maps;
import org.purah.core.base.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;
import org.purah.core.matcher.singlelevel.WildCardMatcher;
import org.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * People people=new People(id:123,name:123,address:123,child:[new People(id:0,name:null),new People(id:1,name:null)]);
 * GeneralFieldMatcher "na*|address|noExistField|child#*.id|child#5.child#5.id|child#*.child#4.id"
 * return{"name":123,"address":123,noExistField:null,"child#0.id":0,"child#1.id":1,"child#5.child#5.id":null}
 * checker will check "noExistField" "child#5.child#5.id" as null even not exist because it is fixed
 * <p>
 * no field match  child#*.child#4.id so ignore
 */
@Name("general")
public class GeneralFieldMatcher extends AbstractMultilevelFieldMatcher<MultilevelFieldMatcher> {


    protected boolean isFixed;
    protected boolean childIsWildCard;
    protected boolean childIsMultiLevel;


    public GeneralFieldMatcher(String matchStr) {
        super(matchStr);
        isFixed = !isWildCardMatcher(childStr) && !isWildCardMatcher(firstLevelStr);
        if (childStr == null) {
            childIsWildCard = false;
            childIsMultiLevel = false;
        } else {
            childIsWildCard = isWildCardMatcher(childStr);
            childIsMultiLevel = childStr.contains(".") || childStr.contains("#");
        }
    }

    public static boolean isWildCardMatcher(String s) {
        if (!StringUtils.hasText(s)) {
            return false;
        }
        return s.contains("*") || s.contains("+") || s.contains("[") || s.contains("{") || s.contains("?") || s.contains("^") || s.contains("!");
    }

    @Override
    protected IDefaultFieldMatcher initFirstLevelFieldMatcher(String str) {
        return new WildCardMatcher(str);
    }


    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = new HashSet<>();
        if (wrapChildList == null) {
            for (String field : fields) {
                if (firstLevelFieldMatcher.match(field, belongInstance)) {
                    result.add(field);
                }
            }
            return result;
        }
        for (MultilevelFieldMatcher multilevelFieldMatcher : wrapChildList) {
            result.addAll(multilevelFieldMatcher.matchFields(fields, belongInstance));
        }
        return result;
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
        if (isFixed) {
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
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.emptyMap();
        }
        if (listIndex == NO_LIST_INDEX) {
            return Collections.emptyMap();
        }
        if (listIndex == OTHER_LIST_MATCH) {
            Map<String, Object> result = Maps.newHashMapWithExpectedSize(objectList.size());
            if (listIndexStr.equals("*")) {
                for (int index = 0; index < objectList.size(); index++) {
                    String fieldStr = "#" + index;
                    result.put(fieldStr, objectList.get(index));
                }
                return result;
            } else {
                for (int index = 0; index < objectList.size(); index++) {
                    String fieldStr = "#" + index;
                    if (this.match(fieldStr, objectList.get(index))) {
                        result.put(fieldStr, objectList.get(index));
                    }
                }
            }
            return result;

        }
        int getIndex = listIndex;
        if (listIndex < 0) {
            getIndex = objectList.size() + listIndex;
        }
        if (listIndex < objectList.size()) {
            return Collections.singletonMap("#" + listIndex, objectList.get(getIndex));
        }
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "GeneralFieldMatcher{" +
                "firstLevelFieldMatcher=" + firstLevelFieldMatcher +
                ", firstLevelStr='" + firstLevelStr + '\'' +
                ", childStr='" + childStr + '\'' +
                '}';
    }

    @Override
    public boolean supportCache() {
        if (wrapChildList != null) {
            for (MultilevelFieldMatcher normalMultiLevelMatcher : wrapChildList) {
                if (!normalMultiLevelMatcher.supportCache()) {
                    return false;
                }
            }
            return true;
        } else {
            if (isWildCardMatcher(fullMatchStr)) {
                return false;
            }
            return !fullMatchStr.contains("#");

        }
    }


}
