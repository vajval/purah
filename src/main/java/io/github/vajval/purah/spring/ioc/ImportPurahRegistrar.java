package io.github.vajval.purah.spring.ioc;


import io.github.vajval.purah.core.matcher.FieldMatcher;
import io.github.vajval.purah.spring.ioc.refresh.PurahContextRefreshEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.core.exception.UnexpectedException;
import io.github.vajval.purah.core.matcher.factory.BaseStringCacheMatcherFactory;
import io.github.vajval.purah.spring.IgnoreBeanOnPurahContext;
import io.github.vajval.purah.spring.EnablePurah;
import io.github.vajval.purah.spring.aop.CheckItAspect;
import io.github.vajval.purah.spring.config.PurahConfiguration;
import io.github.vajval.purah.spring.ioc.ann.ToBaseMatcherFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
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
import java.util.stream.Collectors;

/**
 * @author vajva
 */
public class ImportPurahRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final Logger log = LogManager.getLogger(ImportPurahRegistrar.class);
    protected Environment environment;
    protected ResourceLoader resourceLoader;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {

        boolean checkItAspect = metadata.getAnnotations().get(EnablePurah.class).getBoolean("checkItAspect");
        if (checkItAspect) {
            registry.registerBeanDefinition(CheckItAspect.class.getName(), genericBeanDefinition(CheckItAspect.class));
        }
        registry.registerBeanDefinition(PurahContext.class.getName(), purahContextBeanDefinition(metadata));
        registry.registerBeanDefinition(PurahConfiguration.class.getName(), genericBeanDefinition(PurahConfiguration.class));
        GenericBeanDefinition genericBeanDefinition = genericBeanDefinition(PurahContextRefreshEventHandler.class);
        genericBeanDefinition.setLazyInit(false);

        registry.registerBeanDefinition(PurahContextRefreshEventHandler.class.getName(), genericBeanDefinition);
    }


    public GenericBeanDefinition genericBeanDefinition(Class<?> clazz) {
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(clazz);
        genericBeanDefinition.setLazyInit(true);
        genericBeanDefinition.setPrimary(true);
        genericBeanDefinition.setAutowireCandidate(true);
        return genericBeanDefinition;
    }


    public AbstractBeanDefinition purahContextBeanDefinition(AnnotationMetadata metadata) {

        LinkedHashSet<BeanDefinition> beanDefinitions = filterFieldMatcherByAnn(metadata);

        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(PurahContextFactoryBean.class);

        EnablePurah enablePurah = ((StandardAnnotationMetadata) metadata).getIntrospectedClass().getDeclaredAnnotation(EnablePurah.class);


        List<Class<FieldMatcher>> classes = scanStringMatcherClass(beanDefinitions);

        List<Class<FieldMatcher>> collect = classes.stream().filter(BaseStringCacheMatcherFactory::clazzVerify).collect(Collectors.toList());

        definitionBuilder.addPropertyValue("singleStringConstructorFieldMatcherClassSet", collect);

        definitionBuilder.addPropertyValue("enablePurah", enablePurah);

        definitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);


        definitionBuilder.setLazyInit(true);
        AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setPrimary(true);
        return beanDefinition;
    }


    private LinkedHashSet<BeanDefinition> filterFieldMatcherByAnn(AnnotationMetadata metadata) {
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ToBaseMatcherFactory.class));
        scanner.addExcludeFilter(new AnnotationTypeFilter(IgnoreBeanOnPurahContext.class));
        String packageName = ClassUtils.getPackageName(metadata.getClassName());
        return new LinkedHashSet<>(scanner.findCandidateComponents(packageName));
    }


    private <T> List<Class<T>> scanStringMatcherClass(LinkedHashSet<BeanDefinition> candidateComponents) {
        List<Class<T>> result = new ArrayList<>();
        for (BeanDefinition beanDefinition : candidateComponents) {
            Class<?> clazz;
            try {
                clazz = Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                log.error("Unexpected" + e);
                throw new UnexpectedException(e.getMessage());
            }
            boolean verify = BaseStringCacheMatcherFactory.clazzVerify(clazz);
            if (verify) {
                result.add((Class) clazz);
            }
        }
        return result;
    }


    /*
     * copy from feign
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

}
