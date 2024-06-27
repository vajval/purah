package org.purah.core.checker.converter.checker;

import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.PurahWrapMethod;
import org.purah.core.checker.result.CheckResult;
import org.purah.core.matcher.multilevel.GeneralFieldMatcher;
import org.purah.core.resolver.ArgResolver;
import org.purah.core.resolver.ReflectArgResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FValCheckerByDefaultReflectArgResolver extends AbstractWrapMethodToChecker {
    Map<Integer, FieldParameter> fieldParameterMap = new ConcurrentHashMap<>();

    public static final ArgResolver resolver = new ReflectArgResolver();

    static class FieldParameter {
        int index;
        FVal FVal;

        Class<?> clazz;

        public FieldParameter(int index, FVal FVal, Class<?> clazz) {
            this.index = index;
            this.FVal = FVal;
            this.clazz = clazz;
        }
    }


    protected String matchStr;

    public FValCheckerByDefaultReflectArgResolver(Object methodsToCheckersBean, Method method, String name) {
        super(methodsToCheckersBean, method, name);
        String errorMsg = errorMsgAutoMethodCheckerByDefaultReflectArgResolver(methodsToCheckersBean, method);

        if (errorMsg != null) {
            throw new RuntimeException(errorMsg);
        }

        int rootIndex = -1;
        Set<String> matchStirs = new HashSet<>();
        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            FVal fVal = parameter.getDeclaredAnnotation(FVal.class);
            if (fVal != null) {
                if (fVal.value().toLowerCase(Locale.ROOT).equals(FVal.root)) {
                    rootIndex = index;
                } else {
                    matchStirs.add(fVal.value());
                }

                fieldParameterMap.put(index, new FieldParameter(index, fVal, parameter.getType()));
            }
        }
        matchStr = matchStirs.stream().collect(Collectors.joining("|"));

        purahEnableMethod = new PurahWrapMethod(methodsToCheckersBean, method, rootIndex);


    }

    public static String errorMsgAutoMethodCheckerByDefaultReflectArgResolver(Object methodsToCheckersBean, Method method) {


        if (method.getParameters().length < 1) {
            return "入参至少有1個参数" + method;
        }


        return null;
    }


    @Override
    public CheckResult doCheck(InputToCheckerArg inputToCheckerArg) {


        int length = method.getParameters().length;
        Object[] objects = new Object[length];
        GeneralFieldMatcher generalFieldMatcher = new GeneralFieldMatcher(matchStr);
        Map<String, InputToCheckerArg<?>> matchFieldObjectMap = resolver.getMatchFieldObjectMap(inputToCheckerArg, generalFieldMatcher);
        System.out.println(matchFieldObjectMap.keySet());
        for (int i = 0; i < method.getParameters().length; i++) {
            FieldParameter fieldParameter = fieldParameterMap.get(i);
            if (fieldParameter == null) {
                objects[i] = null;
            } else {
                String value = fieldParameter.FVal.value();
                if (value.toLowerCase(Locale.ROOT).equals(FVal.root)) {
                    objects[i] = inputToCheckerArg;
                    continue;
                }
                InputToCheckerArg<?> childArg = matchFieldObjectMap.get(fieldParameter.FVal.value());
                if (childArg == null || childArg.isNull()) {
                    objects[i] = null;
                    continue;
                }
                if (fieldParameter.clazz.isAnnotation()) {
                    objects[i] = childArg.annOnField((Class) fieldParameter.clazz);
                    continue;
                }
                if (!fieldParameter.clazz.isAssignableFrom(childArg.argClass())) {
                    throw new RuntimeException("无法支持" + "获取到的参数class为" + childArg.argClass() + "函数" + method.toGenericString() + "index:" + i + "    只支持" + fieldParameter.clazz);
                }
                objects[i] = childArg.argValue();

            }
        }
        return purahEnableMethod.invokeResult(inputToCheckerArg, objects);

    }


}
