package io.github.vajval.purah.core.matcher.nested;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.inft.ListIndexMatcher;
import io.github.vajval.purah.core.matcher.inft.MultilevelFieldMatcher;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.*;


/*
 * 输入什么字段就获取什么字段
 * 没有这个字段,或这list长度够不到指定要获取的下标
 * 也返回这个字段,用null填充这个字段值
 *
 * People people=new People(id:123,name:123,address:123,child:[new People(id:123,name:null)]);
 * FixedMatcher "name|address|noExistField|child#0.id|child#5.child#0.id"
 * return{"name":123,"address":123,noExistField:null,"child#0.id":123,"child#5.child#0.id":null}
 * checker will check "noExistField" "child#5.child#0.id" as null even not exist
 */

@Name("fixed")
public class FixedMatcher extends BaseStringMatcher implements ListIndexMatcher, MultilevelFieldMatcher {

    protected Map<String, Set<MatchStrS>> allMap;
    protected Map<String, NestedMatchInfo> nestedMatchInfoMap;
    protected int resultExpectedSize = 1;

    protected MatchStrS matchStrS;
    protected NestedMatchInfo nestedMatchInfo;



    public FixedMatcher(String matchStr) {
        super(matchStr);
        if (matchStr.contains("|")) {
            List<String> strings = Splitter.on("|").splitToList(matchStr);
            Set<MatchStrS> matchStrSSet = strings.stream().map(MatchStrS::new).collect(toSet());
            allMap = matchStrSSet.stream().collect(groupingBy(i -> i.firstLevelStr, mapping(i -> i, toSet())));
            resultExpectedSize = allMap.size();
            nestedMatchInfoMap = new HashMap<>();
            for (Map.Entry<String, Set<MatchStrS>> entry : allMap.entrySet()) {
                String matchedField = entry.getKey();
                List<FieldMatcher> childFieldMatchers = entry.getValue().stream()
                        .filter(i -> i.childStr != null)
                        .map(i -> new FixedMatcher(i.childStr))
                        .collect(toList());
                boolean needCollected = entry.getValue().stream()
                        .filter(i -> i.childStr == null)
                        .map(i -> i.firstLevelStr)
                        .collect(toSet())
                        .contains(matchedField);
                nestedMatchInfoMap.put(matchedField, NestedMatchInfo.create(needCollected, childFieldMatchers));
            }
        } else {
            matchStrS = new MatchStrS(matchStr);
            if (matchStrS.childStr == null) {
                nestedMatchInfo = NestedMatchInfo.justCollected;
            } else {
                nestedMatchInfo = NestedMatchInfo.justNested(new FixedMatcher(matchStrS.childStr));
            }
        }
    }


    @Override

    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        if (matchStrS != null) {
            if (fields.contains(matchStrS.fullMatchStr)) {
                return Collections.singleton(matchStrS.fullMatchStr);
            }
            if (fields.contains(matchStrS.firstLevelStr)) {
                return Collections.singleton(matchStrS.firstLevelStr);
            }
            return Collections.singleton(matchStrS.fullMatchStr);
        }

        Set<String> result = Sets.newHashSetWithExpectedSize(resultExpectedSize);
        for (Map.Entry<String, Set<MatchStrS>> entry : allMap.entrySet()) {
            if (fields.contains(entry.getKey())) {
                result.add(entry.getKey());
            } else {
                for (MatchStrS matchStrS : entry.getValue()) {
                    result.add(matchStrS.fullMatchStr);
                }
            }
        }
        resultExpectedSize = result.size();
        return result;
    }

    @Override

    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        if (Objects.equals(matchedField, matchStr)) {
            return NestedMatchInfo.justCollected;
        }
        if (nestedMatchInfo != null) {
            return nestedMatchInfo;
        }
        NestedMatchInfo result = nestedMatchInfoMap.get(matchedField);
        if (result != null) {
            return result;
        }
        return NestedMatchInfo.ignore;

    }

    @Override
    public boolean match(String field, Object belongInstance) {
        if (matchStrS != null) {
            return Objects.equals(field, matchStrS.firstLevelStr);
        }
        return allMap.containsKey(field);
    }

    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        Integer listIndex = matchStrS.listIndex;
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.singletonMap("#" + listIndex, null);
        }
        if (listIndex == MatchStrS.NO_LIST_INDEX) {
            return Collections.emptyMap();
        }
        int getIndex = listIndex;
        if (listIndex < 0) {
            getIndex = objectList.size() + listIndex;
        }
        if (listIndex < objectList.size()) {
            return Collections.singletonMap("#" + listIndex, objectList.get(getIndex));
        }
        return Collections.singletonMap("#" + listIndex, null);
    }


}

