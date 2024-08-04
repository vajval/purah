package io.github.vajval.purah.core.matcher.nested;

import io.github.vajval.purah.core.matcher.FieldMatcher;

import java.util.Collections;
import java.util.List;

public class NestedMatchInfo {

    protected final List<FieldMatcher> childFieldMatcher;
    protected final boolean needCollected;

    private NestedMatchInfo(boolean needCollected, List<FieldMatcher> childFieldMatcher) {
        this.childFieldMatcher = childFieldMatcher;
        this.needCollected = needCollected;
    }

    public static final NestedMatchInfo justCollected = new NestedMatchInfo(true, null);
    public static NestedMatchInfo ignore = new NestedMatchInfo(false, null);


    public static NestedMatchInfo create(boolean needCollected, List<FieldMatcher> childFieldMatcher) {
        return new NestedMatchInfo(needCollected, childFieldMatcher);

    }

    public static NestedMatchInfo needCollectedAndMatchNested(FieldMatcher childFieldMatcher) {
        return new NestedMatchInfo(true, Collections.singletonList(childFieldMatcher));

    }

    public static NestedMatchInfo needCollectedAndMatchNested(List<FieldMatcher> childFieldMatcher) {
        return new NestedMatchInfo(true, childFieldMatcher);

    }


    public static NestedMatchInfo justNested(FieldMatcher childFieldMatcher) {
        return new NestedMatchInfo(false, Collections.singletonList(childFieldMatcher));
    }

    public static NestedMatchInfo justNested(List<FieldMatcher> childFieldMatcher) {
        return new NestedMatchInfo(false, childFieldMatcher);
    }


    public List<FieldMatcher> getNestedFieldMatcherList() {
        return childFieldMatcher;
    }

    public boolean isNeedCollected() {
        return needCollected;
    }

}
