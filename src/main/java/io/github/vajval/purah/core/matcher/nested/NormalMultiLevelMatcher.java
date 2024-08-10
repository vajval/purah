package io.github.vajval.purah.core.matcher.nested;

import com.google.common.collect.Maps;

import com.google.common.collect.Sets;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.inft.ListIndexMatcher;
import io.github.vajval.purah.core.matcher.inft.MultilevelFieldMatcher;
import io.github.vajval.purah.core.name.Name;
import org.springframework.util.CollectionUtils;

import java.util.*;


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
public class NormalMultiLevelMatcher extends BaseNestMatcher implements ListIndexMatcher, MultilevelFieldMatcher {


    public NormalMultiLevelMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    protected FieldMatcher wrapChild(String str) {
        return new NormalMultiLevelMatcher(str);
    }

    @Override
    public boolean supportCache() {
        return !matchStr.contains("#") && !matchStr.contains(".");
    }

    @Override

    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        if (belongInstance == null) {
            return new HashSet<>(0);
        }
        if (matchStrS != null) {
            if (fields.contains(matchStrS.fullMatchStr)) {
                return Sets.newHashSet(matchStrS.fullMatchStr);
            }
            if (fields.contains(matchStrS.firstLevelStr)) {
                return Sets.newHashSet(matchStrS.firstLevelStr);
            }
            return Sets.newHashSet();
        }
        Set<String> result = new HashSet<>(resultExpectedSize);
        for (String s : allMap.keySet()) {
            if (fields.contains(s)) {
                result.add(s);
            }
        }
        resultExpectedSize = result.size();
        return result;
    }

    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        if (CollectionUtils.isEmpty(objectList) || listMatchIndexSet.size() == 0) {
            return new HashMap<>(0);
        }
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(listMatchIndexSet.size());
        for (Integer listIndex : listMatchIndexSet) {
            int getIndex = listIndex;
            if (listIndex < 0) {
                getIndex = objectList.size() + listIndex;
            }
            if (listIndex < objectList.size()) {
                result.put("#" + listIndex, objectList.get(getIndex));
            }
        }
        return result;
    }


}

