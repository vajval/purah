package io.github.vajval.purah.core.checker.converter.checker;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.PurahWrapMethod;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.exception.init.InitCheckerException;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.core.matcher.factory.BaseMatcherFactory;
import io.github.vajval.purah.core.matcher.nested.FixedMatcher;
import io.github.vajval.purah.core.name.NameUtil;
import io.github.vajval.purah.core.resolver.ArgResolver;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.matcher.nested.GeneralFieldMatcher;
import io.github.vajval.purah.core.resolver.ReflectArgResolver;
import org.springframework.util.CollectionUtils;

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
    private final FieldMatcher mainFieldMatcher;
    private ArgResolver resolver = defaultResolver;
    private Purahs purahs;

    public FValMethodChecker(Object methodsToCheckersBean, Method method, String name, AutoNull autoNull, Purahs purahs) {
        this(methodsToCheckersBean, method, name, autoNull);
        this.resolver = purahs.argResolver();
        this.purahs = purahs;
    }

    public FValMethodChecker(Object methodsToCheckersBean, Method method, String name, AutoNull autoNull) {
        super(methodsToCheckersBean, method, name, autoNull);
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
                //todo
                FieldMatcher selfFieldMatcher = null;
                Class<? extends BaseStringMatcher> selfClass = fVal.matcher();
                if (!Objects.equals(selfClass, FixedMatcher.class)) {
                    selfFieldMatcher = new BaseMatcherFactory(selfClass).create(fVal.value());
                } else if (Map.class.equals(parameter.getType()) || Set.class.equals(parameter.getType())) {
                    selfFieldMatcher = this.generalFieldMatcher(fVal.value());
                } else {
                    if (fVal.value().toLowerCase(Locale.ROOT).equals(FVal.root)) {
                        rootIndex = index;
                    } else {
                        matchStirs.add(fVal.value());
                    }
                }
                fieldParameterMap.put(index, new FieldParameter(index, fVal, parameter.getType(), selfFieldMatcher, resolver, method));
            }
        }
        String matchStr = String.join("|", matchStirs);
        mainFieldMatcher = this.fixedMatcher(matchStr);
        purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method, rootIndex);
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
    public CheckResult<Object> methodDoCheck(InputToCheckerArg<Object> inputToCheckerArg) {
        int length = method.getParameters().length;
        Object[] objects = new Object[length];
        Map<String, InputToCheckerArg<?>> mainObjectMap = resolver.getMatchFieldObjectMap(inputToCheckerArg, mainFieldMatcher);
        for (int index = 0; index < method.getParameters().length; index++) {
            FieldParameter fieldParameter = fieldParameterMap.get(index);
            if (fieldParameter == null) {
                objects[index] = null;
            } else {
                String value = fieldParameter.FVal.value();
                if (value.toLowerCase(Locale.ROOT).equals(FVal.root)) {
                    objects[index] = inputToCheckerArg;
                } else if (fieldParameter.selfFieldMatcher == null) {
                    objects[index] = fieldParameter.selectFromMainResult(mainObjectMap);
                } else {
                    objects[index] = fieldParameter.resolverBySelfMatcher(inputToCheckerArg);
                }
            }
        }
        return purahEnableMethod.invokeResult(inputToCheckerArg, objects);
    }

    static class FieldParameter {
        final int index;
        final FVal FVal;
        final Class<?> clazz;
        final FieldMatcher selfFieldMatcher;
        final ArgResolver resolver;
        final String methodLog;


        public FieldParameter(int index, FVal FVal, Class<?> clazz, FieldMatcher selfFieldMatcher, ArgResolver resolver, Method method) {
            this.index = index;
            this.FVal = FVal;
            this.clazz = clazz;
            this.selfFieldMatcher = selfFieldMatcher;
            this.resolver = resolver;
            this.methodLog = method.toGenericString();
        }

        protected Object selectFromMainResult(Map<String, InputToCheckerArg<?>> matchFieldObjectMap) {
            InputToCheckerArg<?> childArg = matchFieldObjectMap.get(FVal.value());
            if (childArg == null || childArg.isNull()) {
                return null;
            }
            if (clazz.isAnnotation()) {
                return childArg.annOnField((Class) clazz);
            }
            if (!clazz.isAssignableFrom(childArg.argClass())) {
                throw new InitCheckerException("method cannot support arg[" + index + "] class: " + childArg.argClass() + " param class: " + clazz.getName() + "      method:  " + methodLog);
            }
            return childArg.argValue();
        }

        protected Object resolverBySelfMatcher(InputToCheckerArg<Object> inputToCheckerArg) {
            Map<String, InputToCheckerArg<?>> map = resolver.getMatchFieldObjectMap(inputToCheckerArg, selfFieldMatcher);
            if (clazz.equals(Map.class)) {
                Map<String, Object> objectMap = Maps.newHashMapWithExpectedSize(map.size());
                map.forEach((a, b) -> objectMap.put(a, b.argValue()));
                return objectMap;
            } else if (clazz.equals(Set.class)) {
                Set<Object> set = Sets.newHashSetWithExpectedSize(map.size());
                map.values().forEach(w -> set.add(w.argValue()));
                return set;
            } else {
                if (CollectionUtils.isEmpty(map)) {
                    return null;
                } else if (map.size() == 1) {
                    InputToCheckerArg<?> childArg = map.values().iterator().next();
                    if (!clazz.isAssignableFrom(childArg.argClass())) {
                        throw new InitCheckerException("method cannot support arg[" + index + "] class: " + childArg.argClass() + " param class: " + clazz.getName() + "      method:  " + methodLog);
                    }
                    return childArg.argValue();
                } else {
                    throw new RuntimeException();
                }
            }
        }
    }
}
