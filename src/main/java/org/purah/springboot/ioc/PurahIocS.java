package org.purah.springboot.ioc;

import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.base.Checker;
import org.purah.core.checker.base.Checkers;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.factory.MethodToCheckerFactory;
import org.purah.core.checker.method.toChecker.MethodToChecker;
import org.purah.springboot.ann.EnableBeanOnPurahContext;
import org.purah.springboot.ann.PurahMethodsRegBean;
import org.purah.springboot.ann.convert.ToChecker;
import org.purah.springboot.ann.convert.ToCheckerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurahIocS {
    private final ListableBeanFactory applicationContext;
    private final Map<Object, List<Method>> toCheckerMethodMap;
    private final Map<Object, List<Method>> toCheckeFactroyMethodMap;
    private final Set<Object> purahEnableMethodsBean;
    Log log = LogFactory.getLog(this.getClass());

    Log log() {
        if (this.log == null) {

            this.log = LogFactory.getLog(this.getClass());
        }
        return log;
    }


    protected PurahIocS(ListableBeanFactory applicationContext) {
        this.applicationContext = applicationContext;
        this.purahEnableMethodsBean = enableBeanSetByAnn(PurahMethodsRegBean.class);
        this.toCheckerMethodMap = beanEnableMethodMap(ToChecker.class);
        this.toCheckeFactroyMethodMap = beanEnableMethodMap(ToCheckerFactory.class);
    }

    public Set<Object> purahEnableMethodsBean() {
        return purahEnableMethodsBean;
    }


    public List<Checker> checkersByBeanMethod() {
        Set<Class<? extends MethodToChecker>> allMethodToCheckerSet = clazzConfigsOnMethodAnn(toCheckerMethodMap, m -> m.getDeclaredAnnotation(ToChecker.class).value());
        Map<Class<? extends MethodToChecker>, MethodToChecker> classMethodToCheckerMap = classBeanMap(allMethodToCheckerSet, MethodToChecker.class, Checkers.defaultMethodToChecker);


        return checkersByBeanMethod(classMethodToCheckerMap);

    }

    public List<CheckerFactory> checkerFactoriesByBeanMethod() {
        Set<Class<? extends MethodToCheckerFactory>> allMethodToCheckerSet =
                clazzConfigsOnMethodAnn(toCheckeFactroyMethodMap, m -> m.getDeclaredAnnotation(ToCheckerFactory.class).value());
        Map<Class<? extends MethodToCheckerFactory>, MethodToCheckerFactory> classMethodToCheckerFactoryMap =
                classBeanMap(allMethodToCheckerSet, MethodToCheckerFactory.class, Checkers.defaultMethodToCheckerFactory);
        return checkerFactoryListByMethod(classMethodToCheckerFactoryMap);
    }


    protected Map<Object, List<Method>> beanEnableMethodMap(Class<? extends Annotation> annType) {
        Map<Object, List<Method>> result = Maps.newHashMapWithExpectedSize(purahEnableMethodsBean.size());
        for (Object bean : purahEnableMethodsBean) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            List<Method> methodList = Stream.of(clazz.getMethods()).filter(i -> i.getDeclaredAnnotation(annType) != null).collect(Collectors.toList());
            result.put(bean, methodList);
        }
        return result;

    }

    protected List<Checker> checkersByBeanMethod(Map<Class<? extends
            MethodToChecker>, MethodToChecker> classMethodToCheckerMap) {

        List<Checker> result = new ArrayList<>();
        for (Map.Entry<Object, List<Method>> entry : toCheckerMethodMap.entrySet()) {
            Object bean = entry.getKey();
            List<Method> methodList = entry.getValue();
            for (Method method : methodList) {


                ToChecker toCheckerAnn = method.getDeclaredAnnotation(ToChecker.class);
                MethodToChecker methodToChecker = classMethodToCheckerMap.get(toCheckerAnn.value());

                String name = NameUtil.nameByAnnOnMethod(method);
                Checker checker = methodToChecker.toChecker(bean, method, name);
                if(checker!=null){
                    result.add(checker);

                }


            }
        }
        return result;
    }


    public List<CheckerFactory> checkerFactoryListByMethod(Map<Class<? extends
            MethodToCheckerFactory>, MethodToCheckerFactory> classMethodToCheckerFactoryMap) {

        List<CheckerFactory> checkerFactoryList = new ArrayList<>();

        for (Map.Entry<Object, List<Method>> entry : toCheckeFactroyMethodMap.entrySet()) {
            Object bean = entry.getKey();

            List<Method> methodList = entry.getValue();
            for (Method method : methodList) {

                ToCheckerFactory toCheckerFactory = method.getDeclaredAnnotation(ToCheckerFactory.class);
                MethodToCheckerFactory methodToCheckerFactory = classMethodToCheckerFactoryMap.get(toCheckerFactory.value());


                CheckerFactory checkerFactory = methodToCheckerFactory.toCheckerFactory(bean, method, toCheckerFactory.match(), toCheckerFactory.cacheBeCreatedChecker());
                if (checkerFactory != null) {
                    checkerFactoryList.add(checkerFactory);

                }


            }
        }

        return checkerFactoryList;
    }


    private <T> Set<Class<? extends
            T>> clazzConfigsOnMethodAnn(Map<Object, List<Method>> beanMethodMap, Function<Method, Class<? extends
            T>> function) {

        Set<Class<? extends T>> allTClassSet = new HashSet<>();
        for (Map.Entry<Object, List<Method>> entry : beanMethodMap.entrySet()) {
            List<Method> methodList = entry.getValue();
            for (Method method : methodList) {
                Class<? extends T> tClazz = function.apply(method);
                if (tClazz != null) {
                    allTClassSet.add(tClazz);
                }
            }

        }
        return allTClassSet;
    }

    private <T> Map<Class<? extends T>, T> classBeanMap(Set<Class<? extends T>> classSet,
                                                        Class<? extends T> defaultClazz, T defaultValue) {
        HashSet<Class<? extends T>> allClassSet = new HashSet<>(classSet);
        allClassSet.remove(defaultClazz);
        Map<Class<? extends T>, T> classMethodToCheckerMap = new HashMap<>();
        for (Class<? extends T> methodToCheckerClazz : allClassSet) {
            T bean = applicationContext.getBean(methodToCheckerClazz);
            classMethodToCheckerMap.put(methodToCheckerClazz, bean);
        }
        classMethodToCheckerMap.put(defaultClazz, defaultValue);
        return classMethodToCheckerMap;

    }


    public Set<Object> enableBeanSetByAnn(Class<? extends Annotation> annType) {
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(annType).values();
        return filterEnableBean(beans);
    }

    public <T> Set<T> enableBeanSetByClass(Class<T> searchBeanClazz) {
        Collection<T> beans = applicationContext.getBeansOfType(searchBeanClazz).values();
        return filterEnableBean(beans);
    }

    protected static <T> Set<T> filterEnableBean(Collection<T> beans) {
        Set<T> result = new HashSet<>();
        for (T bean : beans) {
            Class<?> beanClazz = AopUtils.getTargetClass(bean);
            EnableBeanOnPurahContext enableBeanOnPurahContext = beanClazz.getDeclaredAnnotation(EnableBeanOnPurahContext.class);
            if (enableBeanOnPurahContext == null) {
                continue;
            }
            result.add(bean);
        }
        return result;
    }


}
