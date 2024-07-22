package org.purah.core.matcher;

import com.google.common.base.Splitter;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 支持一个字符串生成一堆FieldMatcher,这一堆FieldMatcher被一个FieldMatcher封装了
 * A single string can create multiple field matchers.
 */


public abstract class WrapListFieldMatcher<T extends IDefaultFieldMatcher> extends BaseStringMatcher {

    protected List<T> wrapChildList;

    public WrapListFieldMatcher(String matchStr) {
        super(matchStr);
        initWapChildList(matchStr);

    }

    protected void initWapChildList(String matchStr) {
        if (matchStr.contains(wrapSplitStr())) {
            wrapChildList = Splitter.on(wrapSplitStr()).splitToList(matchStr).stream().map(this::wrapChildMatcher).collect(Collectors.toList());
        }
    }
    protected String wrapSplitStr(){
        return "|";
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

    @Override
    public boolean supportCache() {
        if (wrapChildList != null) {
            for (FieldMatcher fieldMatcher : wrapChildList) {
                if (!fieldMatcher.supportCache()) {
                    return false;
                }
            }
            return true;
        } else {
            return supportCacheBySelf();
        }
    }

    protected abstract boolean matchStrCanCache(String matchSer);

    protected boolean supportCacheBySelf() {
        return matchStrCanCache(this.matchStr);
    }

    protected abstract T wrapChildMatcher(String matchStr);

    protected abstract boolean matchBySelf(String field, Object belongInstance);


}
