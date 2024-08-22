package io.github.vajval.purah.spring.ioc.refresh;

import io.github.vajval.purah.core.PurahContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * 刷新 容器时调用
 */


public class PurahContextRefreshEventHandler implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        PurahContext purahContext;
        try {
            purahContext = applicationContext.getBean(PurahContext.class);
        } catch (Exception e) {
            return;
        }
        PurahRefreshHelper purahRefreshHelper = new PurahRefreshHelper(purahContext, applicationContext);
        purahRefreshHelper.refresh();
    }
}
