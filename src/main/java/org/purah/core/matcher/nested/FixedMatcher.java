package org.purah.core.matcher.nested;

import com.google.common.collect.Sets;
import org.purah.core.name.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.singlelevel.EqualMatcher;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.*;


/**
 * People people=new People(id:123,name:123,address:123,child:[new People(id:123,name:null)]);
 * FixedMatcher "name|address|noExistField|child#0.id|child#5.child#0.id"
 * return{"name":123,"address":123,noExistField:null,"child#0.id":123,"child#5.child#0.id":null}
 * checker will check "noExistField" "child#5.child#0.id" as null even not exist
 */

@Name("fixed")
public class FixedMatcher extends AbstractMultilevelFieldMatcher<FixedMatcher> {

    // child.id|child.name|name->{child=[child.id,child.name],name=name}
    protected Map<String, Set<String>> firstLevelToFullMap;
    protected int resultExpectedSize = 1;


    public FixedMatcher(String matchStr) {
        super(matchStr);
        if (wrapChildList != null) {
            firstLevelToFullMap = wrapChildList.stream().collect(groupingBy(i -> i.firstLevelStr, mapping(i -> i.fullMatchStr, toSet())));
            resultExpectedSize = wrapChildList.size();
        }
    }

    protected FixedMatcher wrapChildMatcher(String matchStr) {
        return new FixedMatcher(matchStr);
    }

    @Override
    protected IDefaultFieldMatcher initFirstLevelFieldMatcher(String str) {
        return new EqualMatcher(str);
    }

    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.singletonMap("#" + listIndex, null);
        }
        if (listIndex == NO_LIST_INDEX) {
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


    @Override
    protected boolean supportCacheBySelf() {
        return !fullMatchStr.contains("#");
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {

        if (wrapChildList == null) {
            if (fields.contains(fullMatchStr)) {
                return Collections.singleton(fullMatchStr);
            }
            if (fields.contains(firstLevelStr)) {
                return Collections.singleton(firstLevelStr);
            }
            return Collections.singleton(fullMatchStr);
        }

        Set<String> result = Sets.newHashSetWithExpectedSize(resultExpectedSize);
        for (Map.Entry<String, Set<String>> entry : firstLevelToFullMap.entrySet()) {
            if (fields.contains(entry.getKey())) {
                result.add(entry.getKey());
            } else {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public NestedMatchInfo nestedFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg) {
        if (Objects.equals(matchedField, fullMatchStr)) {
            return NestedMatchInfo.addToResult();
        }
        return super.nestedFieldMatcher(inputArg, matchedField, childArg);

    }
}
