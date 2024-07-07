//////package org.purah.core.matcher.multilevel;
//////
//////public class Test {
//////}
////package org.purah.core.matcher.multilevel;
////
////
////import com.google.common.base.Splitter;
////import org.purah.core.base.Name;
////import org.purah.core.matcher.base.FieldMatcher;
////import org.purah.core.matcher.WildCardMatcher;
////import org.springframework.util.CollectionUtils;
////import org.springframework.util.StringUtils;
////
////import java.util.ArrayList;
////import java.util.Collections;
////import java.util.List;
////import java.util.Objects;
////import java.util.stream.Collectors;
////
//@Name("general")
//public class GeneralFieldMatcher extends AbstractMultilevelFieldMatcher {
//
//    FieldMatcher firstLevelFieldMatcher;
//    String firstLevelStr;
//    String childStr;
//
//    List<GeneralFieldMatcher> wrapList = null;
//
//
//    public GeneralFieldMatcher(String matchStr) {
//        super(matchStr);
//        if (this.matchStr.contains("|")) {
//            wrapList = Splitter.on("|").splitToList(matchStr.substring(1, matchStr.length() - 1)).stream().map(GeneralFieldMatcher::new).collect(Collectors.toList());
//            return;
//        }
//
//        int index = matchStr.indexOf(".");
//        firstLevelStr = matchStr;
//        childStr = "";
//        if (index != -1) {
//            firstLevelStr = matchStr.substring(0, index);
//            childStr = matchStr.substring(index + 1);
//        }
//        index = firstLevelStr.indexOf("#");
//        if (index != -1 && index != 0) {
//            childStr = firstLevelStr.substring(index) + "." + childStr;
//            firstLevelStr = matchStr.substring(0, index);
//        }
//        if (!StringUtils.hasText(childStr)) {
//            childStr = null;
//        }
//        firstLevelFieldMatcher = new WildCardMatcher(firstLevelStr);
//    }
//
//    @Override
//    public boolean match(String field, Object belongInstance) {
//        if (firstLevelFieldMatcher != null) {
//            return firstLevelFieldMatcher.match(field);
//        }
//        for (GeneralFieldMatcher generalFieldMatcher : wrapList) {
//            if (generalFieldMatcher.match(field)) {
//                return true;
//            }
//        }
//        return false;
//
//    }
//
//
//    @Override
//    public MultilevelMatchInfo childFieldMatcher(Object instance, String matchedField, Object matchedObject) {
//        if (wrapList != null) {
//            List<FieldMatcher> fieldMatchers = new ArrayList<>();
//            boolean addToFinal = false;
//            for (GeneralFieldMatcher generalFieldMatcher : wrapList) {
//                if (generalFieldMatcher.match(matchedField, instance)) {
//                    MultilevelMatchInfo multilevelMatchInfo = generalFieldMatcher.childFieldMatcher(instance, matchedField, matchedObject);
//                    addToFinal = addToFinal || multilevelMatchInfo.isAddToFinal();
//                    if (multilevelMatchInfo.getChildFieldMatcherList() != null) {
//                        fieldMatchers.addAll(multilevelMatchInfo.getChildFieldMatcherList());
//                    }
//                }
//            }
//            if (addToFinal) {
//                return MultilevelMatchInfo.addToFinalAndChildMatcher(fieldMatchers);
//            }
//            return MultilevelMatchInfo.justChild(fieldMatchers);
//        }
//        if (childStr == null) {
//            return MultilevelMatchInfo.addToFinal();
//        }
//        if (childStr.contains(".") || childStr.contains("#")) {
//            return MultilevelMatchInfo.addToFinalAndChildMatcher(new GeneralFieldMatcher(childStr));
//        }
//        return MultilevelMatchInfo.justChild(new WildCardMatcher(childStr));
//
//////        FieldMatcher fieldMatcher;
//////        if (childStr.contains(".") || childStr.contains("#")) {
//////            fieldMatcher = new GeneralFieldMatcher(childStr);
//////        } else {
//////            fieldMatcher = new WildCardMatcher(childStr);
//////        }
//////        return MultilevelMatchInfo.justChild(fieldMatcher);
////    }
////
////
////    @Override
////    public String toString() {
////        return "GeneralFieldMatcher{" +
////                "firstLevelFieldMatcher=" + firstLevelFieldMatcher +
////                ", firstLevelStr='" + firstLevelStr + '\'' +
////                ", childStr='" + childStr + '\'' +
////                '}';
////    }
////}
////
//package org.purah.core.checker.converter.checker;
//
//import org.purah.core.checker.InputToCheckerArg;
//import org.purah.core.checker.PurahWrapMethod;
//import org.purah.core.checker.result.CheckResult;
//import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
//import org.purah.core.resolver.ArgResolver;
//import org.purah.core.resolver.ReflectArgResolver;
//
//import java.lang.reflect.Method;
//import java.lang.reflect.Parameter;
//import java.util.HashSet;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
//public class FValCheckerByDefaultReflectArgResolver extends AbstractWrapMethodToChecker {
//    Map<Integer, FieldParameter> fieldParameterMap = new ConcurrentHashMap<>();
//
//    public static final ArgResolver resolver = new ReflectArgResolver();
//
//    static class FieldParameter {
//        int index;
//        FVal FVal;
//
//        Class<?> clazz;
//
//        public FieldParameter(int index, FVal FVal, Class<?> clazz) {
//            this.index = index;
//            this.FVal = FVal;
//            this.clazz = clazz;
//        }
//    }
//
//
//    protected String matchStr;
//
//    public FValCheckerByDefaultReflectArgResolver(Object methodsToCheckersBean, Method method, String name) {
//        super(methodsToCheckersBean, method, name);
//        String errorMsg = errorMsgAutoMethodCheckerByDefaultReflectArgResolver(methodsToCheckersBean, method);
//
//        if (errorMsg != null) {
//            throw new RuntimeException(errorMsg);
//        }
//
//        int rootIndex = -1;
//        Set<String> matchStirs = new HashSet<>();
//        Parameter[] parameters = method.getParameters();
//        for (int index = 0; index < parameters.length; index++) {
//            Parameter parameter = parameters[index];
//            FVal fVal = parameter.getDeclaredAnnotation(FVal.class);
//            if (fVal != null) {
//                if (fVal.value().toLowerCase(Locale.ROOT).equals(FVal.root)) {
//                    rootIndex = index;
//                } else {
//                    matchStirs.add(fVal.value());
//                }
//
//                fieldParameterMap.put(index, new FieldParameter(index, fVal, parameter.getType()));
//            }
//        }
//        matchStr = matchStirs.stream().collect(Collectors.joining("|"));
//
//        purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method, rootIndex);
//
//
//    }
//
//    public static String errorMsgAutoMethodCheckerByDefaultReflectArgResolver(Object methodsToCheckersBean, Method method) {
//
//
//        if (method.getParameters().length < 1) {
//            return "入参至少有1個参数" + method;
//        }
//
//
//        return null;
//    }
//
//
//    @Override
//    public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {
//
//
//        int length = method.getParameters().length;
//        Object[] objects = new Object[length];
//        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher(matchStr);
//        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
//        for (int i = 0; i < method.getParameters().length; i++) {
//            FieldParameter fieldParameter = fieldParameterMap.get(i);
//            if (fieldParameter == null) {
//                objects[i] = null;
//            } else {
//                String value = fieldParameter.FVal.value();
//                if (value.toLowerCase(Locale.ROOT).equals(FVal.root)) {
//                    objects[i] = inputToCheckerArg;
//                    continue;
//                }
//                InputToCheckerArg<?> childArg = matchFieldObjectMap.get(fieldParameter.FVal.value());
//                if (childArg == null || childArg.isNull()) {
//                    objects[i] = null;
//                    continue;
//                }
//                if (fieldParameter.clazz.isAnnotation()) {
//                    objects[i] = childArg.annOnField((Class) fieldParameter.clazz);
//                    continue;
//                }
//                if (!fieldParameter.clazz.isAssignableFrom(childArg.argClass())) {
//                    throw new RuntimeException("无法支持" + "获取到的参数class为" + childArg.argClass() + "函数" + method.toGenericString() + "index:" + i + "    只支持" + fieldParameter.clazz);
//                }
//                objects[i] = childArg.argValue();
//
//            }
//        }
//        return purahEnableMethod.invokeResult(inputToCheckerArg, objects);
//
//    }
//
//
//}
//
