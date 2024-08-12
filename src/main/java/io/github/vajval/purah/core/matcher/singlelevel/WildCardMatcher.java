package io.github.vajval.purah.core.matcher.singlelevel;

import com.google.common.base.Splitter;
import io.github.vajval.purah.core.matcher.BaseStringMatcher;
import org.apache.commons.io.FilenameUtils;
import io.github.vajval.purah.core.name.Name;

import java.util.HashSet;
import java.util.Set;

/*
 * a* ->a ab abc
 * a? -> ab ac ad
 * not support [] {}
 */
@Name("wild_card")
public class WildCardMatcher extends BaseStringMatcher {
    Set<String> matchKeyList;


    public WildCardMatcher(String matchStr) {
        super(matchStr);
        matchKeyList = new HashSet<>(Splitter.on("|").splitToList(matchStr));


    }


    @Override
    public Set<String> matchFields(Set<String> fields, Object belongInstance) {
        Set<String> result = new HashSet<>();
        for (String field : fields) {
            for (String matchKey : matchKeyList) {
                if (FilenameUtils.wildcardMatch(field, matchKey)) {
                    result.add(field);
                    break;
                }

            }
        }
        return result;
    }

}
