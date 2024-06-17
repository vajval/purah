package org.purah.core.matcher.multilevel;


import org.purah.core.matcher.FieldMatcher;

public interface MultilevelFieldMatcher extends FieldMatcher {

    FieldMatcher childFieldMatcher(String matchedField);

    default String levelSplitStr() {
        return ".";
    }
}
