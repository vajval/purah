package org.purah.controller;

import org.purah.pojo.Blog;
import org.purah.springboot.ann.CheckIt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class TestController {
    @PostMapping
    public void postBlog(@CheckIt("博客发表检测") Blog blog) {

        /*
         * 逻辑什么的
         */
    }
}
