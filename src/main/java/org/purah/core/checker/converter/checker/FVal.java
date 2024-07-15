package org.purah.core.checker.converter.checker;

import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.nested.FixedMatcher;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface FVal {
    //Fill in specified field values or annotations.
    String value();
    //root field
    String root = "#root#";
    Class<? extends BaseStringMatcher> matcher() default FixedMatcher.class;
}
