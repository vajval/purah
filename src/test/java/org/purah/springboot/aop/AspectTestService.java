package org.purah.springboot.aop;

import org.purah.springboot.aop.result.MethodCheckResult;
import org.purah.util.Checkers;
import org.purah.util.User;
import org.purah.springboot.aop.ann.CheckIt;
import org.purah.springboot.aop.ann.FillToMethodResult;
import org.springframework.stereotype.Service;

@Service
public class AspectTestService {
    //"example:1[][*:custom_ann_check;*.*:custom_ann_check]"

    public static final String customSyntax = "example:1[][*:" + Checkers.Name.CUSTOM_ANN_CHECK + ";*.*:" +  Checkers.Name.CUSTOM_ANN_CHECK + "]";


    @FillToMethodResult
    public MethodCheckResult checkThreeUser(@CheckIt("all_field_custom_ann_check") User user0,
                                            User user1,
                                            @CheckIt("all_field_custom_ann_check") User user2) {
        return null;
    }

    @FillToMethodResult
    public MethodCheckResult customSyntax(@CheckIt(customSyntax) User user) {
        return null;
    }

    public void customSyntaxThrowTest(@CheckIt("example:1[][*:custom_ann_check;*.*:custom_ann_check]") User user) {

    }
}
