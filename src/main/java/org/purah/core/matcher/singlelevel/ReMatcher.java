package org.purah.core.matcher.singlelevel;

import org.purah.core.base.Name;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.FieldMatcher;

/**
 * Regular expression matcher, remember to enclose configurations in []
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
