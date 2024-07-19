package org.purah.springboot.aop;

import org.purah.core.PurahContext;
import org.purah.core.exception.UnexpectedException;
import org.purah.core.checker.AbstractBaseSupportCacheChecker;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.MultiCheckerExecutor;
import org.purah.core.checker.result.*;
import org.purah.springboot.aop.ann.CheckIt;
import org.purah.springboot.aop.ann.FillToMethodResult;
import org.purah.springboot.aop.ann.MethodCheck;
import org.purah.springboot.aop.result.ArgCheckResult;
import org.purah.springboot.aop.result.MethodCheckResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodHandlerChecker extends AbstractBaseSupportCacheChecker<Object[], List<ArgCheckResult>> {


    protected final PurahContext purahContext;


    protected final Object bean;

    protected final Method method;
    protected final Type returnType;

    protected String name;

    protected boolean fillToMethodResult;

    protected ExecMode.Main execMode = ExecMode.Main.all_success;

    protected ResultLevel resultLevel = ResultLevel.all;
    protected final Map<Integer, ParameterHandlerChecker> parameterHandlerCheckerMap = new HashMap<>();

    public MethodHandlerChecker(Object bean, Method method, PurahContext purahContext) {
        this.purahContext = purahContext;
        this.bean = bean;
        this.method = method;
        this.returnType = method.getReturnType();
        this.init();

    }


    protected void init() {

        //都没有就是不检测
        FillToMethodResult ann = method.getDeclaredAnnotation(FillToMethodResult.class);
        fillToMethodResult = (ann != null);

        MethodCheck methodCheck = method.getDeclaredAnnotation(MethodCheck.class);
        if (methodCheck != null) {
            execMode = methodCheck.mainMode();
            resultLevel = methodCheck.resultLevel();
        }

        if (!StringUtils.hasText(this.name)) {
            this.name = bean.getClass().getSimpleName() + ":" + method.getName();
        }
        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            CheckIt checkIt = parameter.getDeclaredAnnotation(CheckIt.class);
            if (checkIt == null) continue;

            /*
             * 找到有注解的参数
             * 如果指定了校验规则就按照指定的
             */
            //如果没有指定就看class上面的

            List<String> checkerNameList = Stream.of(checkIt.value()).collect(Collectors.toList());

            if (checkerNameList.size() == 0) {
                CheckIt argClazzCheckIt = parameter.getType().getDeclaredAnnotation(CheckIt.class);
                if (argClazzCheckIt != null) {
                    checkerNameList = Stream.of(argClazzCheckIt.value()).collect(Collectors.toList());
                }
            }
            ParameterHandlerChecker parameterHandlerChecker = new ParameterHandlerChecker(purahContext, parameter, method, checkerNameList, index);
            parameterHandlerCheckerMap.put(index, parameterHandlerChecker);
        }


    }

    @Override
    public MethodCheckResult check(Object[] inputArg) {
        return check(InputToCheckerArg.of(inputArg, inputArgClass()));
    }

    @Override
    public MethodCheckResult doCheck(InputToCheckerArg<Object[]> inputToCheckerArg) {
        Object[] args = inputToCheckerArg.argValue();


        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(execMode, resultLevel);

        for (int index = 0; index < args.length; index++) {

            ParameterHandlerChecker checker = parameterHandlerCheckerMap.get(index);
            if (checker == null) {
                continue;
            }
            Object arg = args[index];
            InputToCheckerArg<Object> parameterArg = InputToCheckerArg.of(arg, checker.argClazz());
            multiCheckerExecutor.add(() -> checker.check(parameterArg));
        }


        String log = bean.getClass() + ":" + method.getName();
        MultiCheckResult<ArgCheckResult> multiCheckResult = (MultiCheckResult) multiCheckerExecutor.toMultiCheckResult(log);
        Iterator<ArgCheckResult> iterator = multiCheckResult.data().iterator();

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


        return new MethodCheckResult(
                multiCheckResult.mainCheckResult(),
                resultValueList, bean, method
        );
    }


    @Override
    public MethodCheckResult check(InputToCheckerArg inputToCheckerArg) {
        return (MethodCheckResult) super.check(inputToCheckerArg);
    }


    public Object fillObject(MethodCheckResult methodCheckResult) {

        if (this.isMethodCheckResultType()) {
            //获取函数检测结果
            return methodCheckResult;
        } else if (this.isBaseLogicResultType()) {
            //获取基础结果
            return methodCheckResult.main();
        }else if (this.isBooleanResultType()) {
            //获取boolean结果
            return methodCheckResult.isSuccess();
        } else {
            throw new UnexpectedException("fillObject");
        }
    }


    @Override
    public String logicFrom() {
        return this.method.getName();
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

    private boolean isArgCheckResultType() {
        return ArgCheckResult.class.equals(returnType);
    }


    private boolean isMethodCheckResultType() {
        return MethodCheckResult.class.equals(returnType) || CheckResult.class.equals(returnType)|| MultiCheckResult.class.equals(returnType);
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


