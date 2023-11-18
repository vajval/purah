package com.purah.matcher.multilevel;


import com.purah.matcher.intf.FieldMatcher;

public interface MultilevelFieldMatcher extends FieldMatcher {

    FieldMatcher childFieldMatcher(String matchedField);

    default String levelSplitStr() {
        return ".";
    }
}
