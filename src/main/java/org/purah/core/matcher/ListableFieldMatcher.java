package org.purah.core.matcher;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.lang.annotation.*;
import java.util.stream.Collectors;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ListableFieldMatcher {

    String pre() default "{";

    String suf() default "}";
    String split() default ",";


}
