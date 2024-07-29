package io.github.vajval.purah.core.checker.ann;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface NotEmptyTest {
    String errorMsg();
}
