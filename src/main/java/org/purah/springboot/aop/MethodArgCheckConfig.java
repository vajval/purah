package org.purah.springboot.aop;

import org.purah.springboot.aop.ann.CheckIt;

import java.util.List;

/**
 * 对函数的一个参数检验的配置
 */
public class MethodArgCheckConfig {

    /**
     * 注解内容
     */
    private CheckIt checkItAnn;
    /**
     * 校验用的规则
     */

    private List<String> checkerNameList;
    /**
     * 入参类型
     */
    private Class<?> clazz;
    /**
     * 入参的位置
     */

    private int index;


    public MethodArgCheckConfig(CheckIt checkItAnn, List<String> checkerNameList, Class<?> clazz, int index) {
        this.checkItAnn = checkItAnn;
        this.checkerNameList = checkerNameList;
        this.clazz = clazz;
        this.index = index;
    }

    public CheckIt checkItAnn() {
        return checkItAnn;
    }


    public List<String> checkerNameList() {
        return checkerNameList;
    }


    public Class<?> argClazz() {
        return clazz;
    }


    public int argIndexInMethod() {
        return index;
    }


}
