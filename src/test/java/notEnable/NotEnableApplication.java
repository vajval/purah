package notEnable;

import org.purah.springboot.EnablePurah;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePurah(checkItAspect = false)
public class NotEnableApplication {

    public static void main(String[] args) {

        SpringApplication.run(NotEnableApplication.class, args);
    }
}
