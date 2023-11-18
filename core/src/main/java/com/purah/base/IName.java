package com.purah.base;


import static com.purah.base.NameUtil.nameByClassNameAnn;

/**
 * 名字接口
 */
public interface IName {
    /**
     * 默认名字从注解获取
     * 也可以自己实现
     */
    default String name() {
        return nameByClassNameAnn(this.getClass());
    }

}
