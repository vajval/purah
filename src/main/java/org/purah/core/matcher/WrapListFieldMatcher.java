package org.purah.core.matcher;

import com.google.common.base.Splitter;
import org.purah.core.matcher.BaseStringMatcher;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;

import java.util.List;
import java.util.stream.Collectors;


/**
 * A single string can create multiple field matchers.
 */


public abstract class WrapListFieldMatcher<T extends IDefaultFieldMatcher> extends BaseStringMatcher {

    protected List<T> wrapChildList;

    public WrapListFieldMatcher(String matchStr) {
        super(matchStr);
        initWapChildList(matchStr);

    }

    protected void initWapChildList(String matchStr) {
        if (matchStr.contains("|")) {
            wrapChildList = Splitter.on("|").splitToList(matchStr).stream().map(this::wrapChildMatcher).collect(Collectors.toList());
        }
    }


    @Override
    public boolean match(String field, Object belongInstance) {
        if (wrapChildList != null) {
            return matchByWrap(field, belongInstance);
        }
        return matchBySelf(field, belongInstance);
    }

    protected boolean matchByWrap(String field, Object belongInstance) {
        for (IDefaultFieldMatcher matcher : wrapChildList) {
            if (matcher.match(field, belongInstance)) {
                return true;
            }
        }
        return false;
    }

    protected abstract T wrapChildMatcher(String matchStr);

    protected abstract boolean matchBySelf(String field, Object belongInstance);


}
