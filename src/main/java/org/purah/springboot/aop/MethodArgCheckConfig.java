package org.purah.springboot.aop;

import org.purah.springboot.ann.CheckIt;

import java.util.List;

/**
 * 对函数的一个参数检验的配置
 */
public class MethodArgCheckConfig {

    /**
     * 注解内容
     */
    CheckIt checkItAnn;
    /**
     * 校验用的规则
     */

    List<String> checkerNameList;
    /**
     * 入参类型
     */
    Class<?> clazz;
    /**
     * 入参的位置
     */

    int index;

    public CheckIt getCheckItAnn() {
        return checkItAnn;
    }

    public void setCheckItAnn(CheckIt checkItAnn) {
        this.checkItAnn = checkItAnn;
    }

    public List<String> getCheckerNameList() {
        return checkerNameList;
    }

    public void setCheckerNameList(List<String> checkerNameList) {
        this.checkerNameList = checkerNameList;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
