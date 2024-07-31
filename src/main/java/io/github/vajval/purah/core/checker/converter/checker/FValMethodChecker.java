package io.github.vajval.purah.core.checker.converter.checker;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.PurahWrapMethod;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.core.name.NameUtil;
import io.github.vajval.purah.core.resolver.ArgResolver;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/*
 * Convert a method like this
 * See unit test
 *
 * public static boolean childNameCheck(@FVal("#root#") InputToCheckerArg<People> peopleArg,
                                         @FVal("name") String name,
                                         @FVal("name") TestAnn testAnnOnNameField,
                                         @FVal("name") Name noExistAnn,
                                         @FVal("child") List<People> childList,
                                         @FVal("child#0") People child0,
                                         @FVal("child#100") People child100,
                                         @FVal("child#0.child") List<People> child0ChildList,
                                         @FVal("child#0.child#0.name") String childChildName,
                                         @FVal("child#0.child#0.child") List<People> superChildList) {
* }
 */

public class FValMethodChecker extends AbstractWrapMethodToChecker {
    private final Map<Integer, FieldParameter> fieldParameterMap = new ConcurrentHashMap<>();
    private static final ArgResolver defaultResolver = new ReflectArgResolver();


    private FieldMatcher fieldMatcher;
    private ArgResolver resolver = defaultResolver;

    private Purahs purahs;

    static class FieldParameter {
        final int index;
        final FVal FVal;
        final Class<?> clazz;
        final FieldMatcher fieldMatcher;

        public FieldParameter(int index, FVal FVal, Class<?> clazz, FieldMatcher fieldMatcher) {
            this.index = index;
            this.FVal = FVal;
            this.clazz = clazz;
            this.fieldMatcher = fieldMatcher;
        }
    }

    public FValMethodChecker(Object methodsToCheckersBean, Method method, String name, Purahs purahs) {
        super(methodsToCheckersBean, method, name);
        this.resolver = purahs.argResolver();
        this.purahs = purahs;
    }

    protected FieldMatcher generalFieldMatcher(String value) {
        if (purahs != null) {
            return purahs.matcherOf(NameUtil.nameByAnnOnClass(GeneralFieldMatcher.class)).create(value);
        }
        return new GeneralFieldMatcher(value);
    }

    protected FieldMatcher fixedMatcher(String value) {
        if (purahs != null) {
            return purahs.matcherOf(NameUtil.nameByAnnOnClass(FixedMatcher.class)).create(value);
        }
        return new FixedMatcher(value);
    }

    public FValMethodChecker(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        String errorMsg = errorMsgAutoMethodCheckerByDefaultReflectArgResolver(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new InitCheckerException(errorMsg);
        }

        int rootIndex = -1;
        Set<String> matchStirs = new HashSet<>();
        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            FVal fVal = parameter.getDeclaredAnnotation(FVal.class);
            if (fVal != null) {
                if (!Map.class.equals(parameter.getType()) && !Set.class.equals(parameter.getType())) {
                    if (fVal.value().toLowerCase(Locale.ROOT).equals(FVal.root)) {
                        rootIndex = index;
                    } else {
                        matchStirs.add(fVal.value());
                    }
                    fieldParameterMap.put(index, new FieldParameter(index, fVal, parameter.getType(), null));
                } else {
                    fieldParameterMap.put(index, new FieldParameter(index, fVal, parameter.getType(), this.generalFieldMatcher(fVal.value())));
                }
            }
        }
        String matchStr = String.join("|", matchStirs);
        fieldMatcher = this.fixedMatcher(matchStr);
        purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method, rootIndex);
    }

    public static String errorMsgAutoMethodCheckerByDefaultReflectArgResolver(Object methodsToCheckersBean, Method method) {

        if (method.getParameters().length < 1) {
            return "Come on, you need at least one parameter, okay? [" + method + "]";
        }
        Class<?> returnType = method.getReturnType();
        if (!(CheckResult.class.isAssignableFrom(returnType)) && !(boolean.class.isAssignableFrom(returnType))) {
            return "Only supports return types of CheckResult or boolean. [" + method + "]";

        }
        return null;
    }


    @Override
    public CheckResult<Object> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {


        int length = method.getParameters().length;
        Object[] objects = new Object[length];
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.getMatchFieldObjectMap(inputToCheckerArg, fieldMatcher);

        for (int index = 0; index < method.getParameters().length; index++) {
            FieldParameter fieldParameter = fieldParameterMap.get(index);
            if (fieldParameter == null) {
                objects[index] = null;
            } else {
                String value = fieldParameter.FVal.value();
                if (value.toLowerCase(Locale.ROOT).equals(FVal.root)) {
                    objects[index] = inputToCheckerArg;
                    continue;
                }
                if (fieldParameter.fieldMatcher == null) {
                    InputToCheckerArg<?> childArg = matchFieldObjectMap.get(fieldParameter.FVal.value());
                    if (childArg == null || childArg.isNull()) {
                        objects[index] = null;
                        continue;
                    }
                    if (fieldParameter.clazz.isAnnotation()) {
                        objects[index] = childArg.annOnField((Class) fieldParameter.clazz);
                        continue;
                    }
                    if (!fieldParameter.clazz.isAssignableFrom(childArg.argClass())) {
                        throw new InitCheckerException("method cannot support arg[" + index + "] class: " + childArg.argClass() + " param class: " + fieldParameter.clazz.getName() + "method:  " + method.toGenericString());
                    }
                    objects[index] = childArg.argValue();
                } else if (fieldParameter.clazz.equals(Map.class)) {
                    Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(inputToCheckerArg, fieldParameter.fieldMatcher);
                    Map<String, Object> objectMap = Maps.newHashMapWithExpectedSize(map.size());
                    map.forEach((a, b) -> objectMap.put(a, b.argValue()));
                    objects[index] = objectMap;
                } else if (fieldParameter.clazz.equals(Set.class)) {
                    Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(inputToCheckerArg, fieldParameter.fieldMatcher);
                    Set<Object> set = Sets.newHashSetWithExpectedSize(map.size());
                    map.values().forEach(w -> set.add(w.argValue()));
                    objects[index] = set;
                }


            }
        }


        return purahEnableMethod.invokeResult(inputToCheckerArg, objects);

    }


}
