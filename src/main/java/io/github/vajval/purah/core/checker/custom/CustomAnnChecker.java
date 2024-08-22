package io.github.vajval.purah.core.checker.custom;


import io.github.vajval.purah.core.checker.*;
import io.github.vajval.purah.core.checker.combinatorial.ExecMode;
import io.github.vajval.purah.core.checker.converter.checker.AutoNull;
import io.github.vajval.purah.core.checker.converter.checker.ByAnnMethodChecker;
import io.github.vajval.purah.core.checker.result.CheckResult;
import io.github.vajval.purah.core.checker.result.MultiCheckResult;
import io.github.vajval.purah.core.checker.result.ResultLevel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/*

   See unit test    MyCustomAnnChecker

   add method to make it work
   like

 * rangeLong (@Range range ,Long num)
 * or
 * rangeInteger (@Range range ,CheckInstance<Integer> num)
 * returnType CheckerResult<?>|boolean
 */

public abstract class CustomAnnChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {
    protected final Map<Class<? extends Annotation>, GenericsProxyChecker> annCheckerMapping = new ConcurrentHashMap<>();
    protected final ExecMode.Main mainExecType;

    protected final ResultLevel resultLevel;

    public CustomAnnChecker(ExecMode.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;
        initMethods();
    }


    protected void initMethods() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            String errorMsg = ByAnnMethodChecker.errorMsgCheckerByAnnMethod(this, method);
            if (errorMsg == null) {
                ByAnnMethodChecker byAnnMethodChecker = new ByAnnMethodChecker(this, method, UUID.randomUUID().toString(), AutoNull.notEnable, "failed");
                String name = this.name() + "[" + byAnnMethodChecker.annClazz() + "]" + "[" + byAnnMethodChecker.inputArgClass() + "]";
                String logicFrom = this.getClass() + "  convert method " + method.getName();
                Class<? extends Annotation> annClazz = byAnnMethodChecker.annClazz();
                ProxyChecker proxyChecker = new ProxyChecker(byAnnMethodChecker, name, logicFrom);
                GenericsProxyChecker genericsProxyChecker = annCheckerMapping.computeIfAbsent(annClazz, i -> GenericsProxyChecker.create(name).addNewChecker(proxyChecker));
                genericsProxyChecker.addNewChecker(proxyChecker);
            }
        }
    }

    @Override
    public MultiCheckResult<CheckResult<?>> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {

        List<Annotation> enableAnnotations = inputToCheckerArg.annListOnField().stream()
                .filter(i -> annCheckerMapping.containsKey(i.annotationType()))
                .collect(Collectors.toList());
        String annListLogStr = enableAnnotations.stream().map(i -> i.annotationType().getSimpleName()).collect(Collectors.joining(",", "[", "]"));
        String log = inputToCheckerArg.fieldPath() + "  @Ann:" + annListLogStr + " : " + this.name();
        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(mainExecType, resultLevel, log);
        for (Annotation enableAnnotation : enableAnnotations) {
            GenericsProxyChecker genericsProxyChecker = annCheckerMapping.get(enableAnnotation.annotationType());
            multiCheckerExecutor.add(genericsProxyChecker, inputToCheckerArg);
        }
        return multiCheckerExecutor.execToMultiCheckResult();
    }


    /*
     * 取消注释实现对每个字段的 嵌套CheckIt 检查
     */
//    public abstract Purahs purahs();

//    public MultiCheckResult<CheckResult<?>> checkItAnn(CheckIt checkIt, InputToCheckerArg<Object> inputToCheckerArg) {
//        Purahs purahs = purahs();
//        String[] checkerNames = checkIt.value();
//        ExecMode.Main checkItMainMode = checkIt.mainMode();
//        ResultLevel checkItResultLevel = checkIt.resultLevel();
//        ComboBuilderChecker checker = purahs.combo(checkerNames).resultLevel(checkItResultLevel).mainMode(checkItMainMode);
//        return checker.check(inputToCheckerArg);
//    }


}