//package org.purah.core.matcher.multilevel;
//
//import com.google.common.base.Splitter;
//import org.purah.core.matcher.FieldMatcher;
//import org.purah.core.matcher.WildCardMatcher;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ComplexMatcher extends AbstractMultilevelFieldMatcher {
//    FieldMatcher firstLevelFieldMatcher;
//    String firstLevelStr;
//    String childStr;
//
//    List<GeneralFieldMatcher> fieldMatchers = new ArrayList<>();
//
//
//    public ComplexMatcher(String matchStr) {
//        super(matchStr);
//        if (matchStr.startsWith("{")) {
//            int endIndex = matchStr.indexOf("}");
//            List<String> strings = Splitter.on(",")
//                    .splitToList(matchStr.substring(1, endIndex));
//            fieldMatchers = strings.stream().map(GeneralFieldMatcher::new).collect(Collectors.toList());
//            if (endIndex != matchStr.length() - 1) {
//                childStr = matchStr.substring(endIndex + 2);
//            }
//
//        } else {
//            int index1 = matchStr.indexOf("#");
//            int index2 = matchStr.indexOf(".");
//            if (index1 == 0) {
//                if (index2 != -1) {
//                    firstLevelStr = matchStr.substring(0, index2);
//                    childStr = matchStr.substring(index2 + 1);
//                } else {
//                    firstLevelStr = matchStr;
//                    childStr = null;
//                }
//
//            } else if (index1 == -1 && index2 == -1) {
//                childStr = null;
//                firstLevelStr = matchStr;
//            } else if (index1 != -1 && index2 != -1) {//.#
//                if (index1 < index2) {
//                    firstLevelStr = matchStr.substring(0, index1);
//                    childStr = matchStr.substring(index1);
//                } else {
//                    firstLevelStr = matchStr.substring(0, index2);
//                    childStr = matchStr.substring(index2 + 1);
//                }
//            } else if (index1 != -1) { //#
//                firstLevelStr = matchStr.substring(0, index1);
//                childStr = matchStr.substring(index1);
//            } else { //.
//                firstLevelStr = matchStr.substring(index2);
//                childStr = matchStr.substring(index2 + 1);
//            }
//            firstLevelFieldMatcher = new WildCardMatcher(firstLevelStr);
//        }
//
//    }
//
//
//    @Override
//    public boolean match(String field) {
//        if (firstLevelFieldMatcher != null) {
//            return firstLevelFieldMatcher.match(field);
//        }
//        for (GeneralFieldMatcher fieldMatcher : fieldMatchers) {
//            if (fieldMatcher.match(field)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean match(String field, Object belongInstance) {
//        return false;
//    }
//
//    @Override
//    public MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject) {
//        return null;
//    }
//
////    @Override
////    public FieldMatcher childFieldMatcher(String matchedField) {
////        if (firstLevelFieldMatcher != null) {
////            if (childStr == null) {
////                return null;
////            }
////            if (childStr.contains(".") || childStr.contains("#")) {
////                return new GeneralFieldMatcher(childStr);
////            }
////            return new WildCardMatcher(childStr);
////        } else {
////
////            String collect = fieldMatchers
////                    .stream().filter(i -> i.match(matchedField))
////                    .map(i -> i.childStr).map(i -> {
////                        if (i == null) return childStr;
////                        else return i;
////                    }).collect(Collectors.joining(","));
////            System.out.println(collect);
////            if (collect.length() == 0) {
////                return null;
////            }
////            return new GeneralFieldMatcher("{" + collect + "}." + childStr);
////        }
////    }
//
//    public String firstLevelStr() {
//        return firstLevelStr;
//    }
//
//    @Override
//    public String toString() {
//        return "GeneralFieldMatcher{" +
//                "firstLevelFieldMatcher=" + firstLevelFieldMatcher +
//                ", firstLevelStr='" + firstLevelStr + '\'' +
//                ", childStr='" + childStr + '\'' +
//                ", fieldMatchers=" + fieldMatchers +
//                '}';
//    }
//}
