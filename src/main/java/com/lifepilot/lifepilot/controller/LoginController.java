package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // LOGIN PAGE
    // =========================
    @GetMapping("/login")
    public String loginPage(Model model) {

        // Fresh login page - no error message
        model.addAttribute("error", null);

        return "login";
    }

    // =========================
    // LOGIN PROCESS
    // =========================
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        // Email does not exist
        if (user == null) {

            model.addAttribute("error", "Email not registered!");
            model.addAttribute("email", email);

            return "login";
        }

        // Password is wrong
        if (!user.getPassword().equals(password)) {

            model.addAttribute("error", "Wrong password!");
            model.addAttribute("email", email);

            return "login";
        }

        // =========================
        // LOGIN SUCCESS
        // =========================

        session.setAttribute("loggedInUser", user);

        return "redirect:/dashboard";
    }

    // =========================
    // LOGOUT
    // =========================
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}