package org.purah.core.matcher.nested;

import org.purah.core.matcher.FieldMatcher;

import java.util.Collections;
import java.util.List;

public class NestedMatchInfo {

    protected List<FieldMatcher> childFieldMatcher;
    protected boolean addToResult;

    private NestedMatchInfo(List<FieldMatcher> childFieldMatcher, boolean addToResult) {
        this.childFieldMatcher = childFieldMatcher;
        this.addToResult = addToResult;
    }

    public static NestedMatchInfo addToResultAndMatchNested(FieldMatcher childFieldMatcher) {
        return new NestedMatchInfo(Collections.singletonList(childFieldMatcher), true);

    }

    public static NestedMatchInfo addToResultAndMatchNested(List<FieldMatcher> childFieldMatcher) {
        return new NestedMatchInfo(childFieldMatcher, true);

    }

    public static NestedMatchInfo addToResult() {
        return new NestedMatchInfo(null, true);

    }

    public static NestedMatchInfo justNested(FieldMatcher childFieldMatcher) {
        return new NestedMatchInfo(Collections.singletonList(childFieldMatcher), false);
    }

    public static NestedMatchInfo justNested(List<FieldMatcher> childFieldMatcher) {
        return new NestedMatchInfo(childFieldMatcher, false);
    }

    public static NestedMatchInfo ignore() {
        return new NestedMatchInfo(null, false);
    }


    public List<FieldMatcher> getNestedFieldMatcherList() {
        return childFieldMatcher;
    }

    public boolean isAddToResult() {
        return addToResult;
    }

}
