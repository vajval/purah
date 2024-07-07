package org.purah.core.matcher;

import org.purah.core.base.Name;
import org.purah.core.matcher.inft.FieldMatcher;

/**
 * 正则匹配器,配置文件写记得加上 []
 * 例如 '[a。*]' : 规则1,规则2
 *
 */
@Name("re")
public class ReMatcher extends BaseStringMatcher implements FieldMatcher {


    public ReMatcher(String matchStr) {
        super(matchStr);
    }

    @Override
    public boolean match(String field, Object belongInstance) {
        if (field == null) field = "";
        return field.matches(this.matchStr);
    }


}
