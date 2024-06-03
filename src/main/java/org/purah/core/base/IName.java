package org.purah.core.base;


import static org.purah.core.base.NameUtil.nameByAnnOnClass;

/**
 * 名字接口
 */
public interface IName {
    /**
     * 默认名字从注解获取
     * 也可以自己实现
     */
    default String name() {
        return nameByAnnOnClass(this.getClass());
    }

}
