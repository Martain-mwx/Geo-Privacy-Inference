package com.mwx.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Author: mwx
 * @Description: TODO 关于页面的控制器
 * @Date: 2022/04/24/20:53
 */
@Controller
public class PageController {

    @RequestMapping("/")
    public String index(Map<String, Object> map) {
        map.put("hello", "你好");
        return "index";
    }

}
