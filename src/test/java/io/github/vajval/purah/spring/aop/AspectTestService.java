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
    public MethodHandlerCheckResult checkThreeUser(@CheckIt("all_field_custom_ann_check") User user0,
                                                   User user1,
                                                   @CheckIt("all_field_custom_ann_check") User user2) {
        return null;
    }

    public int checkOneUserThrow(@CheckIt("all_field_custom_ann_check") User user0) {
        value++;
        return value;
    }

    public void checkThreeUserThrow(@CheckIt("all_field_custom_ann_check") User user0,
                                    @CheckIt("all_field_custom_ann_check") User user1,
                                    @CheckIt("all_field_custom_ann_check") User user2) {
    }

    @FillToMethodResult
    public MethodHandlerCheckResult customSyntax(@CheckIt(customSyntax) User user) {
        return null;
    }

    public void customSyntaxThrowTest(@CheckIt("example:1[][*:custom_ann_check;*.*:custom_ann_check]") User user) {

    }
}
