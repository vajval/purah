package com.purah.controller;

import com.purah.pojo.Blog;
import com.purah.springboot.ann.CheckIt;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.ConstraintValidator;

public class TestController {
    @PostMapping
    public void postBlog(@CheckIt("博客发表检测") Blog blog) {
        ConstraintValidator constraintValidator;

        /*
         * 逻辑什么的
         */
    }
}
