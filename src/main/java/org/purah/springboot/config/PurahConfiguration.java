package org.purah.springboot.config;

import org.purah.core.PurahContext;
import org.purah.core.Purahs;
import org.purah.springboot.aop.CheckItAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PurahConfiguration {
    @Bean
    public Purahs purahs(PurahContext purahContext) {
        return new Purahs(purahContext);
    }


}
