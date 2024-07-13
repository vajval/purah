package org.purah.core.name;



import java.lang.annotation.*;

/**
 * The class implementing IName needs to implement the name interface.
 * This annotation allows for quick and easy setting of a fixed value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface Name {
    String value();
}
