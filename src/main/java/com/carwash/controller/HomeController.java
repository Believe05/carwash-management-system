// HomeController.java
package com.carwash.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String homePage() {
        return "home";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    @GetMapping("/dashboard-redirect")
    public String dashboardRedirect() {
        return "redirect:/login-success";
    }
}

