package org.purah.springboot.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.purah.springboot.ExampleApplication;

@SpringBootTest(classes = ExampleApplication.class)
class PurahConfigPropertiesTest {

    @Autowired
    PurahConfigProperties properties;

    @Test
    void properties() {

    }


}