package notEnable;

import io.github.vajval.purah.util.User;
import io.github.vajval.purah.spring.aop.ann.CheckIt;
import io.github.vajval.purah.spring.aop.ann.FillToMethodResult;
import org.springframework.stereotype.Service;

@Service
public class NoAopService {


    @FillToMethodResult
    public void noEnableTest(@CheckIt("example:1[][*:自定义注解检测;*.*:自定义注解检测]") User user) {
    }




}
