package io.github.vajval.purah.core.matcher.nested;


import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.WrapListFieldMatcher;
import io.github.vajval.purah.core.matcher.inft.ListIndexMatcher;
import io.github.vajval.purah.core.matcher.singlelevel.WildCardMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.matcher.inft.IDefaultFieldMatcher;
import io.github.vajval.purah.core.matcher.inft.MultilevelFieldMatcher;
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
public class GeneralFieldMatcher extends WrapListFieldMatcher<MultilevelFieldMatcher> implements MultilevelFieldMatcher, ListIndexMatcher {


    protected boolean isFixed;
    protected boolean childIsWildCard;
    protected boolean childIsMultiLevel;
    protected MatchStrS matchStrS;
    protected IDefaultFieldMatcher firstLevelFieldMatcher;

    public GeneralFieldMatcher(String matchStr) {
        super(matchStr);
        if (wrapChildList == null) {
            matchStrS = new MatchStrS(matchStr);
            String childStr = matchStrS.childStr;
            firstLevelFieldMatcher = new WildCardMatcher(matchStrS.firstLevelStr);
            isFixed = !isWildCardMatcher(childStr) && !isWildCardMatcher(matchStrS.firstLevelStr);
            if (childStr == null) {
                childIsWildCard = false;
                childIsMultiLevel = false;
            } else {
                childIsWildCard = isWildCardMatcher(childStr);
                childIsMultiLevel = childStr.contains(".") || childStr.contains("#");
            }
        }
    }



    @Override
    protected void initWapChildList(String matchStr) {

        //todo cache support
        if (matchStr.contains(wrapSplitStr())) {
            Map<Boolean, List<String>> map = Splitter.on(wrapSplitStr()).splitToList(matchStr).stream().collect(Collectors.groupingBy(this::matchStrCanCache));
            List<String> cacheEnableList = map.get(true);
            List<String> noCacheList = map.get(false);
            if (CollectionUtils.isEmpty(cacheEnableList) || CollectionUtils.isEmpty(noCacheList)) {
                wrapChildList = map.values().iterator().next().stream().map(this::wrapChildMatcher).collect(Collectors.toList());
            } else {
                String cacheEnable = String.join(wrapSplitStr(), cacheEnableList);
                String noCache = String.join(wrapSplitStr(), noCacheList);
                wrapChildList = new ArrayList<>();
                wrapChildList.add(wrapChildMatcher(cacheEnable));
                wrapChildList.add(wrapChildMatcher(noCache));
            }
        }
    }


    private static boolean isWildCardMatcher(String s) {
        if (!StringUtils.hasText(s)) {
            return false;
        }
        return s.contains("*") || s.contains("+") || s.contains("[") || s.contains("{") || s.contains("?") || s.contains("^") || s.contains("!");
    }






    @Override
    public boolean matchBySelf(String field, Object belongInstance) {
        return firstLevelFieldMatcher.match(field, belongInstance);
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
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = new HashSet<>();
        if (wrapChildList == null) {
            return firstLevelFieldMatcher.matchFields(fields,belongInstance);
        }
        for (MultilevelFieldMatcher multilevelFieldMatcher : wrapChildList) {
            result.addAll(multilevelFieldMatcher.matchFields(fields, belongInstance));
        }
        return result;
    }


    @Override
    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {

        if (wrapChildList != null) {
            boolean addToFinal = false;
            List<FieldMatcher> fieldMatchers = new ArrayList<>();
            for (MultilevelFieldMatcher optionMatcher : wrapChildList) {
                if (optionMatcher.match(matchedField, inputArg.argValue())) {
                    NestedMatchInfo nestedMatchInfo = optionMatcher.nestedFieldMatcher(inputArg, matchedField, childArg);
                    addToFinal = addToFinal || nestedMatchInfo.isNeedCollected();
                    if (nestedMatchInfo.getNestedFieldMatcherList() != null) {
                        fieldMatchers.addAll(nestedMatchInfo.getNestedFieldMatcherList());
                    }
                }
            }
            if (addToFinal) {
                return NestedMatchInfo.needCollectedAndMatchNested(fieldMatchers);
            }
            return NestedMatchInfo.justNested(fieldMatchers);
        }
        String childStr = matchStrS.childStr;
        if (childStr == null) {
            return NestedMatchInfo.justCollected;
        }
        if (isFixed) {
            return NestedMatchInfo.justNested(new FixedMatcher(childStr));
        }
        if (childIsWildCard) {
            if (childIsMultiLevel) {
                return NestedMatchInfo.justNested(new GeneralFieldMatcher(childStr));
            } else {
                return NestedMatchInfo.justNested(new WildCardMatcher(childStr));
            }
        } else {
            return NestedMatchInfo.justNested(new NormalMultiLevelMatcher(childStr));
        }
    }


    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        Integer listIndex = matchStrS.listIndex;
        String listIndexStr = matchStrS.listIndexStr;
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.emptyMap();
        }
        if (listIndex == MatchStrS.NO_LIST_INDEX) {
            return Collections.emptyMap();
        }
        if (listIndex == MatchStrS.OTHER_LIST_MATCH) {
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
    protected boolean matchStrCanCache(String matchSer) {
        if (matchSer.contains("#")) {
            return false;
        }
        int index = matchSer.indexOf(".");
        if (index == -1) {
            return true;
        }
        return !isWildCardMatcher(matchSer.substring(index + 1));
    }


    @Override
    public String toString() {
        return "GeneralFieldMatcher{" +
                "firstLevelFieldMatcher=" + firstLevelFieldMatcher +
                ", machStr='" +  matchStr+"'}";
    }



}
