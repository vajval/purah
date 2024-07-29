package org.purah.springboot.config;

import org.purah.core.PurahContext;
import org.purah.core.PurahContextConfig;
import org.purah.core.Purahs;
import org.springframework.context.annotation.Bean;


public class PurahConfiguration {
    @Bean
    public Purahs purahs(PurahContext purahContext) {
        return new Purahs(purahContext);
    }

    @Bean
    public PurahContextConfig purahContextConfig(PurahContext purahContext) {
        return purahContext.config();
    }

}
