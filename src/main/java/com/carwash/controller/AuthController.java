package com.carwash.controller;

import com.carwash.dto.UserRegistrationDto;
import com.carwash.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
    
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "signup";
    }
    
    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        
        try {
            userService.registerUser(registrationDto);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }
    
    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/client/dashboard";
    }
}