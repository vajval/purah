package org.purah.springboot.custom.ann;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface NotEmpty {
    String errorMsg();
}
