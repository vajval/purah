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
public class FixedMatcher extends BaseNestMatcher implements ListIndexMatcher, MultilevelFieldMatcher {


    public FixedMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    protected FieldMatcher wrapChild(String str) {
        return new FixedMatcher(str);
    }

    @Override
    public boolean supportCache() {
        return !matchStr.contains("#");
    }


    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        if (matchStrS != null) {
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
    public Map<String, Object> listMatch(List<?> objectList) {
        if (listMatchIndexSet.size() == 0) {
            return new HashMap<>(0);
        }

        Map<String, Object> result = Maps.newHashMapWithExpectedSize(listMatchIndexSet.size());
        for (Integer listIndex : listMatchIndexSet) {
            String key = "#" + listIndex;
            Object value = null;
            if (!CollectionUtils.isEmpty(objectList)) {
                int getIndex = listIndex;
                if (listIndex < 0) {
                    getIndex = objectList.size() + listIndex;
                }
                if (listIndex < objectList.size()) {
                    value = objectList.get(getIndex);
                }
            }

            result.put(key, value);
        }
        return result;
    }


}

