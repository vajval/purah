package io.github.vajval.purah.core.matcher.singlelevel;

import java.lang.annotation.*;


/**
 * "An example provided by default, as seen in the unit test."
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ExampleFieldType {
    String[] value();
}
