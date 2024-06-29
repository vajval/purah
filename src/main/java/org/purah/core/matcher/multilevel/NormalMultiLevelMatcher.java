package org.purah.core.matcher.multilevel;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.purah.core.base.Name;
import org.purah.core.checker.InputToCheckerArg;
import org.purah.core.matcher.FieldMatcher;
import org.purah.core.matcher.ListIndexMatcher;
import org.purah.core.matcher.WildCardMatcher;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
@Name("normal")
public class NormalMultiLevelMatcher extends AbstractMultilevelFieldMatcher  {




    public NormalMultiLevelMatcher(String matchStr) {
        super(matchStr);

    }

    protected MultilevelFieldMatcher wrapChildMatcher(String matchStr) {
        return new NormalMultiLevelMatcher(matchStr);
    }
    @Override
    protected FieldMatcher initFirstLevelFieldMatcher(String str) {
        return new WildCardMatcher(firstLevelStr);
    }



    @Override
    public Map<String, Object> listMatch(List<?> objectList) {
        int index = firstLevelStr.indexOf("#");
        if (index == -1) {
            return Collections.emptyMap();
        }
        String substring = firstLevelStr.substring(index+1);
        int i = Integer.parseInt(substring);
        if (i < objectList.size()) {
            return Collections.singletonMap("#" + i, objectList.get(i));
        }
        return Collections.emptyMap();
    }




}
