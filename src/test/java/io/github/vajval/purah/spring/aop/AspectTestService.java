package io.github.vajval.purah.spring.aop;

import io.github.vajval.purah.spring.aop.result.MethodHandlerCheckResult;
import io.github.vajval.purah.util.Checkers;
import io.github.vajval.purah.util.User;
import io.github.vajval.purah.spring.aop.ann.CheckIt;
import io.github.vajval.purah.spring.aop.ann.FillToMethodResult;
import org.springframework.stereotype.Service;

@Service
public class AspectTestService {
    //"example:1[][*:custom_ann_check;*.*:custom_ann_check]"
    static int value = 0;


    public static final String customSyntax = "example:1[][*|*.*:" + Checkers.Name.CUSTOM_ANN_CHECK + "]";


    @FillToMethodResult
    public MethodHandlerCheckResult checkThreeUser(@CheckIt(CheckItAspectTest.ALL_FIELD_CUSTOM_ANN_CHECK) User user0,
                                                   User user1,
                                                   @CheckIt(CheckItAspectTest.ALL_FIELD_CUSTOM_ANN_CHECK) User user2) {
        return null;
    }

    public void checkOneUserThrow(@CheckIt(CheckItAspectTest.ALL_FIELD_CUSTOM_ANN_CHECK) User user0) {
        value++;
    }

    public void checkThreeUserThrow(@CheckIt(CheckItAspectTest.ALL_FIELD_CUSTOM_ANN_CHECK) User user0,
                                    @CheckIt(CheckItAspectTest.ALL_FIELD_CUSTOM_ANN_CHECK) User user1,
                                    @CheckIt(CheckItAspectTest.ALL_FIELD_CUSTOM_ANN_CHECK) User user2) {
    }

    @FillToMethodResult
    public MethodHandlerCheckResult customSyntax(@CheckIt(customSyntax) User user) {
        return null;
    }

    public void customSyntaxThrowTest(@CheckIt("example:1[][*:" + Checkers.Name.CUSTOM_ANN_CHECK + ";*.*:" + Checkers.Name.CUSTOM_ANN_CHECK + "]") User user) {

    }
}
