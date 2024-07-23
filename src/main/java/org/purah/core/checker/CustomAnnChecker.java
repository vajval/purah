package org.purah.core.checker;


import org.purah.core.PurahContext;
import org.purah.core.Purahs;
import org.purah.core.checker.combinatorial.ExecMode;
import org.purah.core.checker.converter.checker.ByAnnMethodChecker;
import org.purah.core.checker.result.*;
import org.purah.springboot.aop.ann.CheckIt;

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

 * rangeLong (@Range range ,Long num)
 * or
 * rangeInteger (@Range range ,CheckInstance<Integer> num)
 * returnType CheckerResult<?>|boolean
 */

public abstract class CustomAnnChecker extends AbstractBaseSupportCacheChecker<Object, List<CheckResult<?>>> {

    final Map<Class<? extends Annotation>, GenericsProxyChecker> annCheckerMapping = new ConcurrentHashMap<>();

    final ExecMode.Main mainExecType;

    final ResultLevel resultLevel;

    public CustomAnnChecker(ExecMode.Main mainExecType, ResultLevel resultLevel) {
        this.mainExecType = mainExecType;
        this.resultLevel = resultLevel;
        initMethods();
    }


    protected void initMethods() {


        for (Method method : this.getClass().getDeclaredMethods()) {
            String errorMsg = ByAnnMethodChecker.errorMsgCheckerByAnnMethod(this, method);

            if (errorMsg == null) {
                ByAnnMethodChecker byAnnMethodChecker = new ByAnnMethodChecker(this, method, UUID.randomUUID().toString());
                String name = this.name() + "[" + byAnnMethodChecker.annClazz() + "]" + "[" + byAnnMethodChecker.inputArgClass() + "]";
                String logicFrom = this.getClass() + "  convert method " + method.getName();
                Class<? extends Annotation> annClazz = byAnnMethodChecker.annClazz();
                ProxyChecker proxyChecker = new ProxyChecker(byAnnMethodChecker, name, logicFrom);
                GenericsProxyChecker genericsProxyChecker = annCheckerMapping.computeIfAbsent(annClazz, i -> GenericsProxyChecker.create(name).addNewChecker(proxyChecker));
                genericsProxyChecker.addNewChecker(proxyChecker);

            }

        }

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


    @Override
    public MultiCheckResult<CheckResult<?>> doCheck(InputToCheckerArg<Object> inputToCheckerArg) {

        List<Annotation> enableAnnotations = inputToCheckerArg.annListOnField().stream().filter(i -> annCheckerMapping.containsKey(i.annotationType())).collect(Collectors.toList());


        MultiCheckerExecutor multiCheckerExecutor = new MultiCheckerExecutor(mainExecType, resultLevel);

        for (Annotation enableAnnotation : enableAnnotations) {
            multiCheckerExecutor.add(() -> annCheckerMapping.get(enableAnnotation.annotationType()).check(inputToCheckerArg));
        }
        String annListLogStr = enableAnnotations.stream().map(i -> i.annotationType().getSimpleName()).collect(Collectors.joining(",", "[", "]"));
        String log = inputToCheckerArg.fieldPath() + "  @Ann:" + annListLogStr + " : " + this.name();
        return multiCheckerExecutor.toMultiCheckResult(log);


    }


}