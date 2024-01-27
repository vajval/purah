package com.purah.customAnn.pojo;

import com.purah.customAnn.ann.CNPhoneNum;
import com.purah.customAnn.ann.NotEmpty;
import com.purah.customAnn.ann.Range;
import com.purah.springboot.ann.CheckIt;

@CheckIt("所有字段自定义注解检测")
public class CustomUser {
    @Range(min = 1, max = 10, errorMsg = "取值范围错误")
    public Long id;
    @NotEmpty(errorMsg = "这个字段不能为空")
    public String name;
    @CNPhoneNum(errorMsg = "移不动也联不通")
    public String phone;



    CustomUser  childCustomUser;

    public CustomUser(Long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CustomUser getChildCustomUser() {
        return childCustomUser;
    }

    public void setChildCustomUser(CustomUser childCustomUser) {
        this.childCustomUser = childCustomUser;
    }
}

  