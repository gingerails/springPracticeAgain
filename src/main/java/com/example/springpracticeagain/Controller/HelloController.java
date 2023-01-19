package com.example.springpracticeagain.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/h")
    public String home() {
        return "Hello Docker World";
    }
}
