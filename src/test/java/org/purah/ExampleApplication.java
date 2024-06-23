package org.purah;

import org.apache.commons.beanutils.converters.CharacterConverter;
import org.purah.springboot.ann.EnablePurah;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;

@SpringBootApplication
@EnablePurah
public class ExampleApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }
}
