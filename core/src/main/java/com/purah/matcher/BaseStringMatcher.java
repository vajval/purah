package com.purah.matcher;


import com.purah.base.NameUtil;
import com.purah.matcher.intf.FieldMatcher;

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

        return NameUtil.useName(this) + "{" +
                "matchStr='" + matchStr + '\'' +
                '}';
    }
}
