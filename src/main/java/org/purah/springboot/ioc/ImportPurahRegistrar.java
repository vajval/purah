package org.purah.springboot.ioc;


import org.purah.core.PurahContext;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.factory.BaseMatcherFactory;
import org.purah.springboot.ann.IgnoreBeanOnPurahContext;
import org.purah.springboot.ann.EnablePurah;
import org.purah.springboot.ann.convert.ToBaseMatcherFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 注册器将扫描过的class 设置为factoryBean 方便储存
 * 下面代码抄的feign自带的
 *
 * @author vajva
 */
@Configuration
public class ImportPurahRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {


    protected Environment environment;

    protected ResourceLoader resourceLoader;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {

        LinkedHashSet<BeanDefinition> beanDefinitions = enableOnPurahContextCandidateComponent(metadata);


        AbstractBeanDefinition purahContextBeanDefinition = purahContextBeanDefinition(metadata, beanDefinitions);

        registry.registerBeanDefinition(PurahContext.class.getName(), purahContextBeanDefinition);


    }


    public AbstractBeanDefinition purahContextBeanDefinition(AnnotationMetadata metadata, LinkedHashSet<BeanDefinition> beanDefinitions) {


        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(PurahContextFactoryBean.class);

        EnablePurah enablePurah = ((StandardAnnotationMetadata) metadata).getIntrospectedClass().getDeclaredAnnotation(EnablePurah.class);


        List<Class<BaseStringMatcher>> classes = scanStringMatcherClass(beanDefinitions, BaseStringMatcher.class);
        System.out.println("purahContextBeanDefinition");
        System.out.println(classes);
        definitionBuilder.addPropertyValue("baseStringMatcherClass", classes);

        definitionBuilder.addPropertyValue("enablePurah", enablePurah);

        definitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);


        definitionBuilder.setLazyInit(true);
        AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setPrimary(true);
        return beanDefinition;
    }


    private LinkedHashSet<BeanDefinition> enableOnPurahContextCandidateComponent(AnnotationMetadata metadata) {
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ToBaseMatcherFactory.class));
        scanner.addExcludeFilter(new AnnotationTypeFilter(IgnoreBeanOnPurahContext.class));
        String packageName = ClassUtils.getPackageName(metadata.getClassName());
        return new LinkedHashSet<>(scanner.findCandidateComponents(packageName));
    }


    private <T> List<Class<T>> scanStringMatcherClass(LinkedHashSet<BeanDefinition> candidateComponents, Class<T> matchClazz) {
        List<Class<T>> result = new ArrayList<>();
        for (BeanDefinition beanDefinition : candidateComponents) {

            Class<?> clazz;

            try {
                clazz = Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);

            }

            boolean verify = BaseMatcherFactory.clazzVerify(clazz);
            if (verify) {
                result.add((Class) clazz);
            }


        }
        return result;
    }


    /**
     * 看不懂，feign里复制的
     *
     * @return 扫描器
     */


    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

//  ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
//        TypeFilter typeFilter = new AssignableTypeFilter(MyService.class);
//        scanner.addIncludeFilter(typeFilter);
//
//        String basePackage = "com.example.services"; // 指定要扫描的包路径
//        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
//
//        for (BeanDefinition bd : candidateComponents) {
//            try {
//                Class<?> clazz = Class.forName(bd.getBeanClassName());
//                Object target = clazz.getDeclaredConstructor().newInstance();
//                MyServiceCglibProxy cglibProxy = new MyServiceCglibProxy(target);
//                Object proxyBean = cglibProxy.getProxy();
//
//                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
//                builder.setFactoryMethodOnBean("getProxy", "myServiceProxy");
//
//                registry.registerBeanDefinition("myServiceProxy", builder.getBeanDefinition());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//        List<Class<Object>> classes1 = scanPurahInterfaceClass(beanDefinitions);
//
//
//        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
//        genericBeanDefinition.setBeanClass(TestIntf.class);
//
//        genericBeanDefinition.setSource(new TestIntf() {
//
//        });
//        registry.registerBeanDefinition(TestIntf.class.getName(), genericBeanDefinition);
//        BeanDefinitionHolder holder2 = new BeanDefinitionHolder(genericBeanDefinition, TestIntf.class.getName());
//如果我想扫描指定目录下的接口，然后手动将其cglib增强注册到applicationContext中怎么办
//        BeanDefinitionReaderUtils.registerBeanDefinition(holder2, registry);

//        registry.registerBeanDefinition();
}
