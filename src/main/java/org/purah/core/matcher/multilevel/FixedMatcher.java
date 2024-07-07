package org.purah.core.matcher.multilevel;

import com.google.common.collect.Sets;
import org.purah.core.base.Name;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;
import org.purah.core.matcher.WildCardMatcher;

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
    Map<String, Set<String>> firstLevelToFullMap;
    int resultExpectedSize = 1;



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
        return new WildCardMatcher(str);
    }

    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        int index = firstLevelStr.indexOf("#");
        if (index == -1) {
            return Collections.emptyMap();
        }
        String substring = firstLevelStr.substring(index + 1);
        int i = Integer.parseInt(substring);

        if(i !=listIndex){
            throw new RuntimeException("123");
        }
        if (i < objectList.size()) {
            return Collections.singletonMap("#" + i, objectList.get(i));
        }
        return Collections.singletonMap("#" + i, null);
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {


        if (wrapChildList == null) {
            if (fields.contains(firstLevelStr)) {
                return  Collections.singleton(firstLevelStr);
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
}
