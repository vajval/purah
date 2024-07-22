package org.purah.core.matcher.nested;

import org.purah.core.matcher.FieldMatcher;

import java.util.Collections;
import java.util.List;

public class NestedMatchInfo {

    protected final List<FieldMatcher> childFieldMatcher;
    protected final boolean needCollected;

    private NestedMatchInfo(List<FieldMatcher> childFieldMatcher, boolean needCollected) {
        this.childFieldMatcher = childFieldMatcher;
        this.needCollected = needCollected;
    }

    public static NestedMatchInfo needCollectedAndMatchNested(FieldMatcher childFieldMatcher) {
        return new NestedMatchInfo(Collections.singletonList(childFieldMatcher), true);

    }

    public static NestedMatchInfo needCollectedAndMatchNested(List<FieldMatcher> childFieldMatcher) {
        return new NestedMatchInfo(childFieldMatcher, true);

    }

    public static NestedMatchInfo needCollected() {
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

    public boolean isNeedCollected() {
        return needCollected;
    }

}
