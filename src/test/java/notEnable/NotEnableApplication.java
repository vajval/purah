package notEnable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnablePurah(argResolverFastInvokeCache = true)
public class NotEnableApplication {

    public static void main(String[] args) {

        SpringApplication.run(NotEnableApplication.class, args);
    }
}
