package org.purah.core.matcher.singlelevel;


import org.purah.core.name.Name;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;

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
