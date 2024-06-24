package org.purah.core.matcher;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.io.FilenameUtils;
import org.purah.core.base.Name;
import org.purah.core.matcher.BaseStringMatcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通配符 匹配器
 */
@ListableFieldMatcher
@Name("wild_card")
public class WildCardMatcher extends BaseStringMatcher {


    List<String> wildCardList;

    public WildCardMatcher(String matchStr) {
        super(matchStr);
        if (this.matchStr.startsWith("{")) {
            if(!this.matchStr.endsWith("}")){
                throw new RuntimeException("括号要有始有终");
            }
            this.wildCardList = Splitter.on(",").splitToList(this.matchStr.substring(1, this.matchStr.length() - 1));
        } else {
            this.wildCardList = Collections.singletonList(this.matchStr);
        }


    }

    public WildCardMatcher(List<String> wildCardList) {
        super(wildCardList.stream().collect(Collectors.joining(",", "{", "}")));
        this.wildCardList = wildCardList;


    }

    @Override
    public boolean supportCache() {
        return super.supportCache();
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        for (String wildCard : this.wildCardList) {
            if (FilenameUtils.wildcardMatch(field, wildCard)) {
                return true;
            }
        }
        return false;
    }


}
