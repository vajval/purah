package io.github.vajval.purah.spring.ioc.test_bean;

import io.github.vajval.purah.spring.config.PurahConfigProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(value = "purah")
@Configuration
public class PurahConfigPropertiesBean extends PurahConfigProperties {

}