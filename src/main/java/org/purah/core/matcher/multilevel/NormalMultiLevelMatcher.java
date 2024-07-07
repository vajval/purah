package org.purah.core.matcher.multilevel;

import org.purah.core.base.Name;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;
import org.purah.core.matcher.WildCardMatcher;

import java.util.*;
import java.util.stream.Collectors;


/**
 * People people=new People(id:123,name:123,address:123,child:[new People(id:123,name:null)]);
 * FixedMatcher "name|address|noExistField|child#0.id|child#5.child#0.id"
 * return{"name":123,"address":123,"child#0.id":123}
 * checker not check "noExistField" "child#5.child#0.id" because not exist
 */
@Name("normal")
public class NormalMultiLevelMatcher extends AbstractMultilevelFieldMatcher<NormalMultiLevelMatcher> {

    Set<String> firstLevelStrSet;

    public NormalMultiLevelMatcher(String matchStr) {
        super(matchStr);
        if (wrapChildList != null) {
            firstLevelStrSet = wrapChildList.stream().map(i -> i.firstLevelStr).collect(Collectors.toSet());
        } else {
            firstLevelStrSet = Collections.singleton(firstLevelStr);
        }


    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = new HashSet<>();
        for (String s : firstLevelStrSet) {
            if (fields.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }


    protected NormalMultiLevelMatcher wrapChildMatcher(String matchStr) {
        return new NormalMultiLevelMatcher(matchStr);
    }

    @Override
    protected IDefaultFieldMatcher initFirstLevelFieldMatcher(String str) {
        return new WildCardMatcher(firstLevelStr);
    }


    @Override
    public Map<String, Object> listMatch(List<?> objectList) {

        if (listIndex < objectList.size()) {
            return Collections.singletonMap("#" + listIndex, objectList.get(listIndex));
        }
        return Collections.emptyMap();
    }


}
