package org.purah.core.matcher.multilevel;

import org.purah.core.matcher.FieldMatcher;

import java.util.Collections;
import java.util.List;

public class MultilevelMatchInfo {
    List<FieldMatcher> childFieldMatcher;

    boolean addToFinal;

    public MultilevelMatchInfo(List<FieldMatcher> childFieldMatcher, boolean addToFinal) {
        this.childFieldMatcher = childFieldMatcher;
        this.addToFinal = addToFinal;
    }

    public static MultilevelMatchInfo addToFinalAndChildMatcher(FieldMatcher childFieldMatcher) {
        return new MultilevelMatchInfo(Collections.singletonList(childFieldMatcher), true);

    }

    public static MultilevelMatchInfo addToFinalAndChildMatcher(List<FieldMatcher> childFieldMatcher) {
        return new MultilevelMatchInfo(childFieldMatcher, true);

    }

    public static MultilevelMatchInfo addToFinal() {
        return new MultilevelMatchInfo(null, true);

    }

    public static MultilevelMatchInfo justChild(FieldMatcher childFieldMatcher) {
        return new MultilevelMatchInfo(Collections.singletonList(childFieldMatcher), false);
    }

    public static MultilevelMatchInfo justChild(List<FieldMatcher> childFieldMatcher) {
        return new MultilevelMatchInfo(childFieldMatcher, false);
    }

    public static MultilevelMatchInfo ignore() {
        return new MultilevelMatchInfo(null, false);
    }


    public List<FieldMatcher> getChildFieldMatcherList() {
        return childFieldMatcher;
    }

    public boolean isAddToFinal() {
        return addToFinal;
    }
}
