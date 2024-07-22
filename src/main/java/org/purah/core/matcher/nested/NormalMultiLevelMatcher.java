package org.purah.core.matcher.nested;

import org.purah.core.name.Name;
import org.purah.core.matcher.singlelevel.EqualMatcher;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/*
 * 输入字段,获取存在的字段
 * 没有这个字段,或这list长度够不到指定要获取的下标
 * 那就不返回这些不存在的字段
 *
 * People people=new People(id:123,name:123,address:123,child:[new People(id:123,name:null)]);
 * FixedMatcher "name|address|noExistField|child#0.id|child#5.child#0.id"
 * return{"name":123,"address":123,"child#0.id":123}
 * checker not check "noExistField" "child#5.child#0.id" because not exist
 */
@Name("normal")
public class NormalMultiLevelMatcher extends AbstractMultilevelFieldMatcher<NormalMultiLevelMatcher> {


    // child.id|child.name|name->{child,name}
    final Set<String> firstLevelStrSet;

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

    @Override
    protected NormalMultiLevelMatcher wrapChildMatcher(String matchStr) {
        return new NormalMultiLevelMatcher(matchStr);
    }

    @Override
    protected IDefaultFieldMatcher initFirstLevelFieldMatcher(String str) {
        return new EqualMatcher(firstLevelStr);
    }


    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.emptyMap();
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
        return Collections.emptyMap();
    }

    @Override
    protected boolean matchStrCanCache(String matchSer) {
        return !matchSer.contains("#") && !matchSer.contains(".");
    }


}

