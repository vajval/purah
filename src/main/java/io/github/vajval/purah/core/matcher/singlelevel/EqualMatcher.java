package io.github.vajval.purah.core.matcher.singlelevel;


import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;

import java.util.Objects;

@Name("equal")
public class EqualMatcher extends BaseStringMatcher implements FieldMatcher {


    public EqualMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        return Objects.equals(matchStr,field);
    }
}
