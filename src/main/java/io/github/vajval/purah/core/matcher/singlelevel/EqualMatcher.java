package io.github.vajval.purah.core.matcher.singlelevel;


import com.google.common.base.Splitter;
import io.github.vajval.purah.core.name.Name;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;

import java.util.HashSet;
import java.util.Set;

@Name("equal")
public class EqualMatcher extends BaseStringMatcher {
    Set<String> valueList;


    public EqualMatcher(String matchStr) {
        super(matchStr);
        valueList = new HashSet<>(Splitter.on("|").splitToList(matchStr));

    }

    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = new HashSet<>();
        for (String field : fields) {
            if (valueList.contains(field)) {
                result.add(field);
            }
        }
        return result;
    }
}
