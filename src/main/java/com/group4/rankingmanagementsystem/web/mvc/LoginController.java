package com.group4.rankingmanagementsystem.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/mylogin")
public class LoginController {

    @GetMapping
    public String login(){
        return "auth/login";
    }

}
