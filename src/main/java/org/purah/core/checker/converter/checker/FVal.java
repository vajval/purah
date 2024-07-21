package org.purah.core.checker.converter.checker;

import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.nested.FixedMatcher;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface FVal {
    /*
       class People{
       @IdAnn("1")
       Long id=1;
       }
       @FVal("id")  Long id,//id=1
       @FVal("id")  IdAnn idAnn,//idAnn=  @IdAnn("1")
     */



    String value();

    //root field
    String root = FieldMatcher.rootField;

    Class<? extends BaseStringMatcher> matcher() default FixedMatcher.class;
}
