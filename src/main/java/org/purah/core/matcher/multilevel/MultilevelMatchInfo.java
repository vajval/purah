package org.purah.core.matcher.multilevel;

import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;

import java.util.Collections;
import java.util.List;

public class MultilevelMatchInfo {


    List<FieldMatcher> childFieldMatcher;
    InputToCheckerArg<?> addToFinalArg;

    protected MultilevelMatchInfo(List<FieldMatcher> childFieldMatcher, InputToCheckerArg<?> addToFinalArg) {
        this.childFieldMatcher = childFieldMatcher;
        this.addToFinalArg = addToFinalArg;
    }


    public static MultilevelMatchInfo addToFinalAndChildMatcher(FieldMatcher childFieldMatcher, InputToCheckerArg<?> inputToCheckerArg) {
        return new MultilevelMatchInfo(Collections.singletonList(childFieldMatcher), inputToCheckerArg);

    }

    public static MultilevelMatchInfo addToFinalAndChildMatcher(List<FieldMatcher> childFieldMatcher, InputToCheckerArg<?> inputToCheckerArg) {
        return new MultilevelMatchInfo(childFieldMatcher, inputToCheckerArg);

    }

    public static MultilevelMatchInfo addToFinal(InputToCheckerArg<?> inputToCheckerArg) {
        return new MultilevelMatchInfo(null, inputToCheckerArg);

    }

    public static MultilevelMatchInfo justChild(FieldMatcher childFieldMatcher) {
        return new MultilevelMatchInfo(Collections.singletonList(childFieldMatcher), null);
    }

    public static MultilevelMatchInfo justChild(List<FieldMatcher> childFieldMatcher) {
        return new MultilevelMatchInfo(childFieldMatcher, null);
    }

    public static MultilevelMatchInfo ignore() {
        return new MultilevelMatchInfo(null, null);
    }


    public List<FieldMatcher> getChildFieldMatcherList() {
        return childFieldMatcher;
    }

    public boolean isAddToFinal() {
        return addToFinalArg != null;
    }

    public InputToCheckerArg<?> getAddToFinalArg() {
        return addToFinalArg;
    }
}
