package com.genki.rest_api.diary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("/test")
    public String index() {
        return "index";
    }
}
