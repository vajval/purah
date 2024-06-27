package org.purah.springboot.ioc;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purah.core.PurahContext;
import org.purah.core.base.NameUtil;
import org.purah.core.checker.Checker;
import org.purah.core.checker.factory.CheckerFactory;
import org.purah.core.checker.converter.MethodConverter;
import org.purah.core.matcher.MatcherManager;
import org.purah.springboot.ann.IgnoreBeanOnPurahContext;
import org.purah.springboot.ann.PurahMethodsRegBean;
import org.purah.springboot.ann.convert.ToChecker;
import org.purah.springboot.ann.convert.ToCheckerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.purah.core.PurahContext.*;

public class PurahIocS {

    private static final Logger logger = LogManager.getLogger(PurahIocS.class);

    private final ListableBeanFactory applicationContext;
    private final Map<Object, List<Method>> toCheckerMethodMap;
    private final Map<Object, List<Method>> toCheckeFactroyMethodMap;
    private final Set<Object> purahEnableMethodsBean;
    protected MethodConverter methodConverter;


    protected PurahIocS(ListableBeanFactory applicationContext) {
        this.applicationContext = applicationContext;
        this.purahEnableMethodsBean = enableBeanSetByAnn(PurahMethodsRegBean.class);
        this.toCheckerMethodMap = beanEnableMethodMap(ToChecker.class);
        this.toCheckeFactroyMethodMap = beanEnableMethodMap(ToCheckerFactory.class);
        try {
            this.methodConverter = applicationContext.getBean(MethodConverter.class);
        } catch (NoSuchBeanDefinitionException ignored) {
            logger.info("使用默认CheckerManager");
            this.methodConverter = DEFAULT_METHOD_CONVERTER;
        }
    }

    public Set<Object> purahEnableMethodsBean() {
        return purahEnableMethodsBean;
    }

    public List<Checker> checkersByBeanMethod() {
        List<Checker> result = new ArrayList<>();
        for (Map.Entry<Object, List<Method>> entry : toCheckerMethodMap.entrySet()) {
            Object bean = entry.getKey();
            List<Method> methodList = entry.getValue();
            for (Method method : methodList) {
                String name = NameUtil.nameByAnnOnMethod(method);
                Checker checker = methodConverter.toChecker(bean, method, name);
                if (checker != null) {
                    result.add(checker);
                }

            }
        }
        return result;
    }


    public List<CheckerFactory> checkerFactoriesByBeanMethod() {
        List<CheckerFactory> checkerFactoryList = new ArrayList<>();
        for (Map.Entry<Object, List<Method>> entry : toCheckeFactroyMethodMap.entrySet()) {
            Object bean = entry.getKey();
            List<Method> methodList = entry.getValue();
            for (Method method : methodList) {
                ToCheckerFactory toCheckerFactory = method.getDeclaredAnnotation(ToCheckerFactory.class);
                CheckerFactory checkerFactory = methodConverter.toCheckerFactory(bean, method, toCheckerFactory.match(), toCheckerFactory.cacheBeCreatedChecker());
                if (checkerFactory != null) {
                    checkerFactoryList.add(checkerFactory);

                }
            }
        }

        return checkerFactoryList;
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
            IgnoreBeanOnPurahContext ignoreBeanOnPurahContext = beanClazz.getDeclaredAnnotation(IgnoreBeanOnPurahContext.class);
            if (ignoreBeanOnPurahContext != null) {
                continue;
            }
            result.add(bean);
        }
        return result;
    }


}
