package com.purah.controller;

import com.purah.pojo.Blog;
import com.purah.springboot.ann.CheckIt;
import org.springframework.web.bind.annotation.PostMapping;

public class TestController {
    @PostMapping
    public void postBlog(@CheckIt("博客发表检测") Blog blog) {

        /*
         * 逻辑什么的
         */
    }
}
