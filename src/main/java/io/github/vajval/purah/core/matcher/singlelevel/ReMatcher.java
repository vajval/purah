package io.github.vajval.purah.core.matcher.singlelevel;

import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;

/**
 * For example, '[a.*]' : Rule 1, Rule 2
 *
 */
@Name("re")
public class ReMatcher extends BaseStringMatcher implements FieldMatcher {


    public ReMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        if (field == null) field = "";
        return field.matches(this.matchStr);
    }


}
