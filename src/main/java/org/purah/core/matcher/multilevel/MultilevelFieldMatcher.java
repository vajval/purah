package org.purah.core.matcher.multilevel;


import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;

public interface MultilevelFieldMatcher extends FieldMatcher {

      MultilevelMatchInfo childFieldMatcher(InputToCheckerArg<?> inputArg, String matchedField, InputToCheckerArg<?> childArg);


}
