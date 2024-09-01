package io.github.vajval.purah.core.matcher.nested;


import com.google.common.base.Splitter;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.inft.ListIndexMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.matcher.inft.MultilevelFieldMatcher;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/*
 * 对于有通配符的当作 normal
 * 没有通配符 写死的 当作 fixed


 * People people=new People(id:123,name:123,address:123,child:[new People(id:0,name:null),new People(id:1,name:null)]);
 * GeneralFieldMatcher "na*|address|noExistField|child#*.id|child#5.child#5.id|child#*.child#4.id"
 * return{"name":123,"address":123,noExistField:null,"child#0.id":0,"child#1.id":1,"child#5.child#5.id":null}
 * checker will check "noExistField" "child#5.child#5.id" as null even not exist because it is fixed
 * <p>
 * no field match  child#*.child#4.id so ignore
 */
@Name("general")
public class GeneralFieldMatcher extends BaseStringMatcher implements MultilevelFieldMatcher, ListIndexMatcher {
    protected NestedMatchInfo nestedMatchInfo;
    protected FixedMatcher fixedMatcher;
    protected final Map<String, FieldMatcher> firstLevelStrEqualMap;
    protected final Map<String, FieldMatcher> firstLevelStrMatchMap;
    protected final List<String> thisLevelWildCardMatchStrList;
    protected final Set<Integer> listIndexSet;
    protected final Set<String> otherListIndexSet;

    protected final boolean supportCache;

    public GeneralFieldMatcher(String matchStr) {
        super(matchStr);
        String fixValue = Splitter.on("|").splitToList(matchStr).stream().filter(i -> !isWildCardMatcher(i)).collect(Collectors.joining("|"));
        if (StringUtils.hasText(fixValue)) {
            fixedMatcher = new FixedMatcher(fixValue);
        }
        List<MatchStrS> wildCardMatchStrSList = Splitter.on("|").splitToList(matchStr).stream().filter(GeneralFieldMatcher::isWildCardMatcher).map(MatchStrS::new).collect(Collectors.toList());
        listIndexSet = wildCardMatchStrSList.stream().map(i -> i.listIndex).collect(Collectors.toSet());
        thisLevelWildCardMatchStrList = wildCardMatchStrSList.stream().filter(i -> i.childStr == null).map(i -> i.fullMatchStr).collect(Collectors.toList());
        List<MatchStrS> haveChildMatchStrS = wildCardMatchStrSList.stream().filter(i -> i.childStr != null).collect(Collectors.toList());
        Map<String, String> firstEqual = haveChildMatchStrS.stream().filter(i -> !isWildCardMatcher(i.firstLevelStr)).collect(Collectors.groupingBy(i -> i.firstLevelStr, Collectors.mapping(i -> i.childStr, Collectors.joining("|"))));
        Map<String, String> firstMatch = haveChildMatchStrS.stream().filter(i -> isWildCardMatcher(i.firstLevelStr)).collect(Collectors.groupingBy(i -> i.firstLevelStr, Collectors.mapping(i -> i.childStr, Collectors.joining("|"))));
        otherListIndexSet = new HashSet<>();
        firstLevelStrEqualMap = new HashMap<>();
        firstLevelStrMatchMap = new HashMap<>();
        for (Map.Entry<String, String> entry : firstMatch.entrySet()) {
            String matchKey = entry.getKey();
            String childMatchStr = entry.getValue();
            FieldMatcher fieldMatcher = wrapMatchChild(childMatchStr);
            firstLevelStrMatchMap.put(matchKey, fieldMatcher);
            String listIndexStr = new MatchStrS(matchKey).listIndexStr;
            if (StringUtils.hasText(listIndexStr)) {
                otherListIndexSet.add(listIndexStr);
            }
        }
        for (Map.Entry<String, String> entry : firstEqual.entrySet()) {
            String equalKey = entry.getKey();
            String value = entry.getValue();
            firstLevelStrEqualMap.put(equalKey, wrapMatchChild(value));
            listIndexSet.add(new MatchStrS(equalKey).listIndex);
        }
        if (!matchStr.contains("|")) {
            MatchStrS matchStrS = new MatchStrS(matchStr);
            listIndexSet.add(matchStrS.listIndex);
            if (StringUtils.hasText(matchStrS.listIndexStr)) {
                otherListIndexSet.add(matchStrS.listIndexStr);
            }
            nestedMatchInfo = initNestedMatchInfoIfSingle(matchStrS);
        }
        listIndexSet.remove(MatchStrS.NO_LIST_INDEX);
        listIndexSet.remove(MatchStrS.OTHER_LIST_MATCH);
        supportCache = (!this.matchStr.contains("#")) && (this.firstLevelStrMatchMap.size() == 0) && (this.firstLevelStrEqualMap.size() == 0);
    }

    protected FieldMatcher wrapMatchChild(String childMatchStr) {
        return isWildCardMatcher(childMatchStr) ? new GeneralFieldMatcher(childMatchStr) : new NormalMultiLevelMatcher(childMatchStr);
    }

    protected NestedMatchInfo initNestedMatchInfoIfSingle(MatchStrS matchStrS) {
        String childStr = matchStrS.childStr;
        if (childStr == null) {
            return NestedMatchInfo.justCollected;
        } else if (!isWildCardMatcher(matchStrS.fullMatchStr)) {//isFixed
            return NestedMatchInfo.justNested(new FixedMatcher(childStr));
        } else if (isWildCardMatcher(childStr)) {//childIsWildCard
            return NestedMatchInfo.justNested(new GeneralFieldMatcher(childStr));
        } else {
            return NestedMatchInfo.justNested(new NormalMultiLevelMatcher(childStr));
        }
    }


    protected static boolean isWildCardMatcher(String s) {
        if (!StringUtils.hasText(s)) {
            return false;
        }
        return s.contains("*") || s.contains("+") || s.contains("[") || s.contains("{") || s.contains("?") || s.contains("^") || s.contains("!");
    }


    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = (fixedMatcher != null) ? fixedMatcher.matchFields(fields, belongInstance) : new HashSet<>();
        if (belongInstance == null) {
            return result;
        }
        for (String field : fields) {
            if (includeField(field)) {
                result.add(field);
            }
        }
        return result;
    }

    protected boolean includeField(String field) {
        if (firstLevelStrEqualMap.containsKey(field)) {
            return true;
        }
        for (String thisLevelWildCardMatch : thisLevelWildCardMatchStrList) {
            if (fieldByMatchKey(field, thisLevelWildCardMatch)) {
                return true;
            }
        }
        for (String matchKey : firstLevelStrMatchMap.keySet()) {
            if (fieldByMatchKey(field, matchKey)) {
                return true;
            }
        }
        return false;
    }

    private boolean fieldByMatchKey(String field, String matchKey) {
        return FilenameUtils.wildcardMatch(field, matchKey);
    }


    @Override
    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        if (nestedMatchInfo != null) {
            return nestedMatchInfo;
        }
        List<FieldMatcher> childFieldMatcher = new ArrayList<>();
        boolean needCollected = false;
        if (fixedMatcher != null) {
            NestedMatchInfo nestedMatchInfo = fixedMatcher.nestedFieldMatcher(inputArg, matchedField, childArg);
            needCollected = nestedMatchInfo.isNeedCollected();
            childFieldMatcher = nestedMatchInfo.getNestedFieldMatcherList();
        }
        if (!needCollected) {
            for (String matchKey : thisLevelWildCardMatchStrList) {
                if (fieldByMatchKey(matchedField, matchKey)) {
                    needCollected = true;
                    break;
                }
            }
        }
        if (childArg.isNull()) {
            return NestedMatchInfo.create(needCollected, childFieldMatcher);
        }
        for (Map.Entry<String, FieldMatcher> entry : firstLevelStrMatchMap.entrySet()) {
            String matchKey = entry.getKey();
            if (fieldByMatchKey(matchedField, matchKey)) {
                childFieldMatcher.add(entry.getValue());
            }
        }
        FieldMatcher fieldMatcher = firstLevelStrEqualMap.get(matchedField);
        if (fieldMatcher != null) {
            childFieldMatcher.add(fieldMatcher);
        }
        return NestedMatchInfo.create(needCollected, childFieldMatcher);
    }


    @Override
    public boolean match(String field, Object belongInstance) {
        return matchFields(Collections.singleton(field), belongInstance).contains(field);
    }


    @Override
    public boolean supportCache() {
        return supportCache;
    }


    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        Map<String, Object> result = fixedMatcher != null ? fixedMatcher.listMatch(objectList) : new HashMap<>();
        if (CollectionUtils.isEmpty(objectList)) {
            return result;
        }
        for (Integer listIndex : listIndexSet) {
            String key = "#" + listIndex;
            Object value = null;
            int getIndex = listIndex;
            if (listIndex < 0) {
                getIndex = objectList.size() + listIndex;
            }
            if (listIndex < objectList.size()) {
                value = objectList.get(getIndex);
            }
            result.put(key, value);
        }
        if (otherListIndexSet.contains("*")) {
            for (int index = 0; index < objectList.size(); index++) {
                String fieldStr = "#" + index;
                result.put(fieldStr, objectList.get(index));
            }
        }
        return result;
    }
}
