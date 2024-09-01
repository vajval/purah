//package io.github.vajval.purah.core.matcher.nested;
//
//import com.google.common.collect.Maps;
//import io.github.vajval.purah.core.matcher.FieldMatcher;
//import org.springframework.util.CollectionUtils;
//
//import java.util.*;
//
//public class FixedGeneralFieldMatcher  extends GeneralFieldMatcher{
//    public FixedGeneralFieldMatcher(String matchStr) {
//
//        super(matchStr);
//    }
//
//    @Override
//    protected FieldMatcher wrapMatchChild(String childMatchStr) {
//        return isWildCardMatcher(childMatchStr) ? new FixedGeneralFieldMatcher(childMatchStr) : new FixedMatcher(childMatchStr);
//    }
//    protected NestedMatchInfo initNestedMatchInfoIfSingle(MatchStrS matchStrS) {
//        String childStr = matchStrS.childStr;
//        if (childStr == null) {
//            return NestedMatchInfo.justCollected;
//        } else if (!isWildCardMatcher(matchStrS.fullMatchStr)) {//isFixed
//            return NestedMatchInfo.justNested(new FixedMatcher(childStr));
//        } else if (isWildCardMatcher(childStr)) {//childIsWildCard
//            return NestedMatchInfo.justNested(new FixedGeneralFieldMatcher(childStr));
//        } else {
//            return NestedMatchInfo.justNested(new FixedMatcher(childStr));
//        }
//    }
//    @Override
//    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
//        Set<String> result = (fixedMatcher != null) ? fixedMatcher.matchFields(fields, belongInstance) : new HashSet<>();
//        for (String field : fields) {
//            if (includeField(field)) {
//                result.add(field);
//            }
//        }
//        return result;
//    }
//    @Override
//    public Map<String, Object> listMatch(List<?> objectList) {
//        Map<String, Object> result = fixedMatcher != null ? fixedMatcher.listMatch(objectList) : new HashMap<>();
//        if (CollectionUtils.isEmpty(objectList)) {
//            return result;
//        }
//        for (Integer listIndex : listIndexSet) {
//            String key = "#" + listIndex;
//            Object value = null;
//            if (!CollectionUtils.isEmpty(objectList)) {
//                int getIndex = listIndex;
//                if (listIndex < 0) {
//                    getIndex = objectList.size() + listIndex;
//                }
//                if (listIndex < objectList.size()) {
//                    value = objectList.get(getIndex);
//                }
//            }
//
//            result.put(key, value);
//        }
//
//        if (otherListIndexSet.contains("*")) {
//            for (int index = 0; index < objectList.size(); index++) {
//                String fieldStr = "#" + index;
//                result.put(fieldStr, objectList.get(index));
//            }
//        }
//        return result;
//    }
//}
//
