package notEnable;

import org.junit.jupiter.api.Test;
import org.purah.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = NotEnableApplication.class)
public class NotEnableTest {
    @Autowired
    NoAopService customService;
    @Test
    public void test()
    {

        customService.noEnableTest(new User(50L, null, "123", null));
    }
}
