package io.github.vajval.purah.spring.config;

import io.github.vajval.purah.core.PurahContext;
import io.github.vajval.purah.core.PurahContextConfig;
import io.github.vajval.purah.core.Purahs;
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
