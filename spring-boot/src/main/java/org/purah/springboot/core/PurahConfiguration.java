package org.purah.springboot.core;


import com.purah.PurahContext;
import com.purah.checker.CheckerManager;
import com.purah.matcher.MatcherManager;
import com.purah.resolver.ArgResolverManager;
import org.purah.springboot.aop.CheckItMethodHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 装载类
 */
@Configuration
public class PurahConfiguration implements ApplicationContextAware {

    ApplicationContext applicationContext;


    @Autowired
    PurahContext purahContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public MatcherManager matcherManager(PurahContext context) {
        return context.matcherManager();
    }


    @Bean
    public ArgResolverManager argResolverManager(PurahContext context) {
        return context.argResolverManager();
    }

    @Bean
    public CheckerManager ruleManager(PurahContext context) {
        return context.checkManager();
    }

    @Bean
    public CheckItMethodHandler checkItMethodHandler(PurahContext context) {
        return new CheckItMethodHandler(context);
    }
}
