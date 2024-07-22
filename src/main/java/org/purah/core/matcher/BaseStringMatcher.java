package org.purah.core.matcher;


import org.purah.core.name.NameUtil;
import org.purah.core.matcher.inft.IDefaultFieldMatcher;

import java.util.Objects;

/**
 * 最基础的,有一个字符串配置就可以创建 FieldMatcher
 * Basic matcher that constructs itself using an input string,
 * allowing it to be configured using information from a configuration file.
 */

public abstract class BaseStringMatcher implements IDefaultFieldMatcher {

    protected final String matchStr;

    public BaseStringMatcher(String matchStr) {
        this.matchStr = matchStr;
    }


    @Override
    public String toString() {
        return NameUtil.logClazzName(this) + ":(  " + matchStr + "  )";
    }

    @Override
    public boolean supportCache() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseStringMatcher that = (BaseStringMatcher) o;
        return Objects.equals(matchStr, that.matchStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchStr);
    }


}
