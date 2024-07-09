package org.purah.core.matcher.singlelevel;

import org.apache.commons.io.FilenameUtils;
import org.purah.core.base.Name;
import org.purah.core.matcher.WrapListFieldMatcher;

/**
 * a* ->a ab abc
 * a? -> ab ac ad
 * not support [] {}
 */
@Name("wild_card")
public class WildCardMatcher extends WrapListFieldMatcher<WildCardMatcher> {


    public WildCardMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    protected WildCardMatcher wrapChildMatcher(String matchStr) {
        return new WildCardMatcher(matchStr);
    }

    @Override
    public boolean matchBySelf(String field, Object belongInstance) {
        return FilenameUtils.wildcardMatch(field, this.matchStr);
    }


}
