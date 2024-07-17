package notEnable;

import org.junit.jupiter.api.Test;
import org.purah.ExampleApplication;
import org.purah.example.customAnn.CustomService;
import org.purah.example.customAnn.pojo.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = NotEnableApplication.class)
public class NotEnableService {
    @Autowired
    NoAopService customService;
    @Test
    public void test(){
        customService.methodCheckByCustomSyntaxWithMultiLevel(new CustomUser(50L, null, "123", null));
    }
}
