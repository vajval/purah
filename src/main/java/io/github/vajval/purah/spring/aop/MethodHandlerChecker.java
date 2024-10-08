package io.github.vajval.purah.spring.aop;

import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.LogicCheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;
import io.github.vajval.purah.core.exception.UnexpectedException;
import io.github.vajval.purah.core.Purahs;
import io.github.vajval.purah.core.checker.AbstractBaseSupportCacheChecker;
import io.github.vajval.purah.core.checker.InputToCheckerArg;
import io.github.vajval.purah.core.checker.MultiCheckerExecutor;
import io.github.vajval.purah.spring.aop.ann.CheckIt;
import io.github.vajval.purah.spring.aop.ann.FillToMethodResult;
import io.github.vajval.purah.spring.aop.ann.MethodCheckConfig;
import io.github.vajval.purah.spring.aop.result.ArgCheckResult;
import io.github.vajval.purah.spring.aop.result.MethodHandlerCheckResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodHandlerChecker extends AbstractBaseSupportCacheChecker<Object[], List<ArgCheckResult>> {


    protected final Object bean;

    protected final Method method;

    protected final Type returnType;

    protected String name;
    protected boolean fillToMethodResult;
    protected ExecMode.Main execMode = ExecMode.Main.all_success;

    protected ResultLevel resultLevel = ResultLevel.all;
    protected final Map<Integer, ParameterHandlerChecker> parameterHandlerCheckerMap = new HashMap<>();

    public MethodHandlerChecker(Object bean, Method method, Purahs purahs) {
        this.bean = bean;
        this.method = method;
        this.returnType = method.getReturnType();
        this.init(purahs);
    }


    /*
      @CheckIt("user")
      class CustomUser{
      }
      class CustomPeople{
      }
     * public void voidCheck(@CheckIt("test") CustomUser customUser) {         //enable test
     * public void voidCheck(@CheckIt CustomUser customUser) {                 //enable user
     * public void voidCheck(@CheckIt("test") CustomPeople CustomPeople) {     //enable test
     * public void voidCheck(@CheckIt CustomPeople CustomPeople) {             //enable nothing

    */
    protected void init(Purahs purahs) {

        //都没有就是不检测
        FillToMethodResult ann = method.getDeclaredAnnotation(FillToMethodResult.class);
        fillToMethodResult = (ann != null);

        MethodCheckConfig methodCheckConfig = method.getDeclaredAnnotation(MethodCheckConfig.class);
        if (methodCheckConfig != null) {
            execMode = methodCheckConfig.mainMode();
            resultLevel = methodCheckConfig.resultLevel();
        }

        if (!StringUtils.hasText(this.name)) {
            this.name = bean.getClass().getSimpleName() + ":" + method.getName();
        }
        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            CheckIt checkIt = parameter.getDeclaredAnnotation(CheckIt.class);
            if (checkIt == null) continue;

            List<String> checkerNameList = Stream.of(checkIt.value()).collect(Collectors.toList());

            if (checkerNameList.size() == 0) {
                CheckIt argClazzCheckIt = parameter.getType().getDeclaredAnnotation(CheckIt.class);
                if (argClazzCheckIt != null) {
                    checkerNameList = Stream.of(argClazzCheckIt.value()).collect(Collectors.toList());
                }
            }
            ParameterHandlerChecker parameterHandlerChecker = new ParameterHandlerChecker(purahs, parameter, method, checkerNameList, index);
            parameterHandlerCheckerMap.put(index, parameterHandlerChecker);
        }


    }

    @Override
    public MethodHandlerCheckResult oCheck(Object[] inputArg) {
        return check(InputToCheckerArg.of(inputArg, inputArgClass()));
    }

    @Override
    public MethodHandlerCheckResult doCheck(InputToCheckerArg<Object[]> inputToCheckerArg) {
        Object[] args = inputToCheckerArg.argValue();
        String log = bean.getClass() + ":" + method.getName();
        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(execMode, resultLevel, log);
        for (int index = 0; index < args.length; index++) {

            ParameterHandlerChecker checker = parameterHandlerCheckerMap.get(index);
            if (checker == null) {
                continue;
            }
            Object arg = args[index];
            InputToCheckerArg<Object> parameterArg = InputToCheckerArg.of(arg, checker.argClazz());
            multiCheckerExecutor.add(checker, parameterArg);
        }
        MultiCheckResult<ArgCheckResult> multiCheckResult = (MultiCheckResult) multiCheckerExecutor.execToMultiCheckResult();
        Iterator<ArgCheckResult> iterator = multiCheckResult.value().iterator();
        List<ArgCheckResult> resultValueList = new ArrayList<>();
        for (int index = 0; index < args.length; index++) {
            ParameterHandlerChecker checker = parameterHandlerCheckerMap.get(index);
            if (checker == null) {
                resultValueList.add(ArgCheckResult.noAnnIgnore());//no ann
            } else {
                if (iterator.hasNext()) {
                    resultValueList.add(iterator.next());
                } else {
                    resultValueList.add(checker.createIgnoreResult(args[index], execMode));//ignore
                }
            }
        }
        return new MethodHandlerCheckResult(multiCheckResult.mainResult(), resultValueList, bean, method);
    }


    @Override
    public MethodHandlerCheckResult check(InputToCheckerArg<Object[]> inputToCheckerArg) {
        return (MethodHandlerCheckResult) super.check(inputToCheckerArg);
    }


    public Object fillObject(MethodHandlerCheckResult methodHandlerCheckResult) {

        if (this.isMethodCheckResultType()) {
            //获取函数检测结果
            return methodHandlerCheckResult;
        } else if (this.isBaseLogicResultType()) {
            //获取基础结果
            return methodHandlerCheckResult.mainResult();
        } else if (this.isBooleanResultType()) {
            //获取boolean结果
            return methodHandlerCheckResult.isSuccess();
        } else {
            throw new UnexpectedException("fillObject");
        }
    }


    @Override
    public String logicFrom() {
        return this.method.toGenericString();
    }

    @Override
    public Class<?> inputArgClass() {
        return Object[].class;
    }

    @Override
    public Class<?> resultDataClass() {
        if (this.returnType instanceof Class) {
            return (Class<?>) this.returnType;
        }
        return super.resultDataClass();
    }


    private boolean isMethodCheckResultType() {
        return MethodHandlerCheckResult.class.equals(returnType) || CheckResult.class.equals(returnType) || MultiCheckResult.class.equals(returnType);
    }

    private boolean isBaseLogicResultType() {
        return LogicCheckResult.class.equals(returnType);
    }

    private boolean isBooleanResultType() {
        return boolean.class.equals(returnType);
    }


    public boolean isFillToMethodResult() {
        return fillToMethodResult;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean enableCache() {
        return false;
    }
}


