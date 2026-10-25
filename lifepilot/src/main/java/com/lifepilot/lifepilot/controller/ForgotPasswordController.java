package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    private final UserRepository userRepository;

    public ForgotPasswordController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // FORGOT PASSWORD PAGE
    // =========================

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // =========================
    // CHECK EMAIL
    // =========================

    @PostMapping("/forgot-password")
    public String checkEmail(
            @RequestParam String email,
            Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {

            model.addAttribute(
                    "error",
                    "Email not registered!");

            return "forgot-password";
        }

        // Email exists
        model.addAttribute("email", email);

        return "reset-password";
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        // Check passwords
        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match!");

            model.addAttribute("email", email);

            return "reset-password";
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {

            return "redirect:/forgot-password";
        }

        // Update password
        user.setPassword(newPassword);

        userRepository.save(user);

        return "redirect:/login";
    }
}