package com.purah.customAnn.pojo;

import com.purah.customAnn.ann.CNPhoneNum;
import com.purah.customAnn.ann.NotEmpty;
import com.purah.customAnn.ann.Range;

public class CustomUser {
    @Range(min = 1, max = 10, errorMsg = "取值范围错误")
    public Long id;
    @NotEmpty(errorMsg = "这个字段不能为空")
    public String name;


    @CNPhoneNum(errorMsg = "移不动也联不通")
    public String phone;

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
}

  