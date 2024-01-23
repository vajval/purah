package org.purah.springboot.core;


import com.purah.PurahContext;
import com.purah.matcher.BaseStringMatcher;
import com.purah.matcher.intf.FieldMatcher;
import org.purah.springboot.ann.EnableOnContext;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
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
//
        List<Class<? extends FieldMatcher>> baseStringMatcherClass = this.scanStringMatcherClass(metadata);


        AbstractBeanDefinition purahContextBeanDefinition = purahContextBeanDefinition(baseStringMatcherClass);


        BeanDefinitionHolder holder = new BeanDefinitionHolder(purahContextBeanDefinition, PurahContext.class.getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);


    }


    public AbstractBeanDefinition purahContextBeanDefinition(List<Class<? extends FieldMatcher>> baseStringMatcherClass) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(PurahContextFactoryBean.class);

        definitionBuilder.addPropertyValue("baseStringMatcherClass", baseStringMatcherClass);
        definitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);


        definitionBuilder.setLazyInit(true);
        AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setPrimary(true);
        return beanDefinition;
    }

    private List<Class<? extends FieldMatcher>> scanStringMatcherClass(AnnotationMetadata metadata) {
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(resourceLoader);

        scanner.addIncludeFilter(new AnnotationTypeFilter(EnableOnContext.class));

        String packageName = ClassUtils.getPackageName(metadata.getClassName());
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>(scanner.findCandidateComponents(packageName));

        List<Class<? extends FieldMatcher>> result = new ArrayList<>();
        for (BeanDefinition beanDefinition : candidateComponents) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                if ((BaseStringMatcher.class.isAssignableFrom(clazz))) {
                    result.add((Class<? extends FieldMatcher>) clazz);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
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




}
