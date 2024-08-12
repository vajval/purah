package io.github.vajval.purah.core.matcher.singlelevel;

import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import io.github.vajval.purah.core.matcher.FieldMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * For example, '[a.*]' : Rule 1, Rule 2
 */
@Name("re")
public class ReMatcher extends BaseStringMatcher {


    public ReMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        if (field == null) field = "";
        return field.matches(this.matchStr);
    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = new HashSet<>();
        for (String field : fields) {
            if (field != null) {
                if (field.matches(this.matchStr)) {
                    result.add(field);
                }
            }
        }
        return result;
    }
}
