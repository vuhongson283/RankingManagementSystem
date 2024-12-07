package com.group4.rankingmanagementsystem.web.mvc;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {


    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/accessDenied"; // Ensure you have an accessDenied.html template in your templates directory
    }


}
