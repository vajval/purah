package org.purah.core.matcher;


import com.google.common.collect.Sets;
import org.purah.core.base.NameUtil;
import org.purah.core.matcher.intf.FieldMatcher;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基础的匹配器，内置一个简单的字符串创建
 * 从配置文件读取的数据都是将其中的内容 传入此类的构造器
 * 当然也可以自定义一些复杂的
 */
public abstract class BaseStringMatcher implements FieldMatcher {
    protected String matchStr;


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
