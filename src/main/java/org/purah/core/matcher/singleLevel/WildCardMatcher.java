package org.purah.core.matcher.singleLevel;

import com.google.common.collect.Sets;
import org.apache.commons.io.FilenameUtils;
import org.purah.core.base.Name;
import org.purah.core.matcher.BaseStringMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * 通配符 匹配器
 */
@Name("wild_card")
public class WildCardMatcher extends BaseStringMatcher {


    String fieldMatchStr;

    public WildCardMatcher(String matchStr) {
        super(matchStr);

        this.fieldMatchStr = this.matchStr;


    }


    @Override
    public boolean supportCache() {
        return super.supportCache();
    }


    @Override
    public boolean match(String field) {
        return FilenameUtils.wildcardMatch(field, fieldMatchStr);
    }


}
