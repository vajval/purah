package io.github.vajval.purah;

import io.github.vajval.purah.spring.EnablePurah;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePurah(argResolverFastInvokeCache = true)
public class ExampleApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExampleApplication.class, args);
    }
}
