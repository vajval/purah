package io.github.vajval.purah.core.matcher.nested;

import com.google.common.base.Splitter;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.inft.ListIndexMatcher;
import io.github.vajval.purah.core.matcher.inft.MultilevelFieldMatcher;

import java.util.*;

import static java.util.stream.Collectors.*;

public abstract class BaseNestMatcher extends BaseStringMatcher implements ListIndexMatcher, MultilevelFieldMatcher {
    protected Map<String, Set<MatchStrS>> allMap;
    protected Map<String, NestedMatchInfo> nestedMatchInfoMap;
    protected int resultExpectedSize = 1;

    protected MatchStrS matchStrS;
    protected NestedMatchInfo nestedMatchInfo;
    protected final Set<Integer> listMatchIndexSet;

    public BaseNestMatcher(String matchStr) {
        super(matchStr);
        if (matchStr.contains("|")) {
            List<String> strings = Splitter.on("|").splitToList(matchStr);

            Set<MatchStrS> matchStrSSet = strings.stream().map(MatchStrS::new).collect(toSet());
            allMap = matchStrSSet.stream().collect(groupingBy(i -> i.firstLevelStr, mapping(i -> i, toSet())));
            resultExpectedSize = allMap.size();
            nestedMatchInfoMap = new HashMap<>();

            for (Map.Entry<String, Set<MatchStrS>> entry : allMap.entrySet()) {

                String matchedField = entry.getKey();
                Set<String> collect = entry.getValue().stream()
                        .filter(i -> i.childStr != null)
                        .map(i -> i.childStr).collect(toSet());
                List<FieldMatcher> childFieldMatchers;
                if (collect.size() > 3) {
                    childFieldMatchers = Collections.singletonList(wrapChild(String.join("|", collect)));
                } else {
                    childFieldMatchers = collect.stream().map(this::wrapChild).collect(toList());
                }
                boolean needCollected = entry.getValue().stream()
                        .filter(i -> i.childStr == null)
                        .map(i -> i.firstLevelStr)
                        .collect(toSet())
                        .contains(matchedField);
                nestedMatchInfoMap.put(matchedField, NestedMatchInfo.create(needCollected, childFieldMatchers));
            }
            listMatchIndexSet = new HashSet<>();

            for (String s : allMap.keySet()) {
                listMatchIndexSet.add(new MatchStrS(s).listIndex);
            }
            listMatchIndexSet.remove(MatchStrS.NO_LIST_INDEX);

        } else {
            matchStrS = new MatchStrS(matchStr);
            if (matchStrS.childStr == null) {
                nestedMatchInfo = NestedMatchInfo.justCollected;
            } else {
                nestedMatchInfo = NestedMatchInfo.justNested(wrapChild(matchStrS.childStr));
            }
            listMatchIndexSet = Collections.singleton(matchStrS.listIndex);
        }
    }

    protected abstract FieldMatcher wrapChild(String str);

    @Override
    public abstract boolean supportCache();

    @Override
    public abstract Set<String> matchFields(Set<String> fields, Object belongInstance);

    @Override
    public abstract Map<String, Object> listMatch(List<?> objectList);

    @Override
    public boolean match(String field, Object belongInstance) {
        if (matchStrS != null) {
            return Objects.equals(field, matchStrS.firstLevelStr);
        }
        return allMap.containsKey(field);
    }


    @Override
    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        if (Objects.equals(matchedField, matchStr)) {
            return NestedMatchInfo.justCollected;
        }
        if (matchStrS != null) {
            if (Objects.equals(matchStrS.firstLevelStr, matchedField)) {
                return nestedMatchInfo;
            }
            return NestedMatchInfo.ignore;
        }
        NestedMatchInfo result = nestedMatchInfoMap.get(matchedField);
        if (result != null) {
            return result;
        }
        return NestedMatchInfo.ignore;

    }


}
