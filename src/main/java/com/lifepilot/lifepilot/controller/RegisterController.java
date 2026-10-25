package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final UserRepository userRepository;

    public RegisterController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Show registration page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Handle registration form
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        // Create user
        User user = new User();
        user.setName(fullName);
        user.setEmail(email);
        user.setPassword(password);

        // Save to MySQL
        userRepository.save(user);

        return "redirect:/login";
    }
}