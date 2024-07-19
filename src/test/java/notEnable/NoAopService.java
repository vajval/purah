package notEnable;

import org.purah.util.User;
import org.purah.springboot.aop.ann.CheckIt;
import org.purah.springboot.aop.ann.FillToMethodResult;
import org.springframework.stereotype.Service;

@Service
public class NoAopService {


    @FillToMethodResult
    public void noEnableTest(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") User user) {
    }




}
