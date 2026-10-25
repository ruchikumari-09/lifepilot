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
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================= PROFILE PAGE =================

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findById(user.getId()).orElse(null);

        if (currentUser == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);

        return "profile";
    }

    // ================= UPDATE PROFILE =================

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            HttpSession session,
            Model model) {

        User sessionUser = (User) session.getAttribute("loggedInUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(sessionUser.getId()).orElse(null);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // Check current password
        if (!user.getPassword().equals(currentPassword)) {

            model.addAttribute("user", user);
            model.addAttribute("error", "Current password is incorrect!");

            return "profile";
        }

        // Update name
        user.setName(name);

        // Change password only if entered
        if (newPassword != null && !newPassword.isEmpty()) {

            if (!newPassword.equals(confirmPassword)) {

                model.addAttribute("user", user);
                model.addAttribute("error", "New passwords do not match!");

                return "profile";
            }

            user.setPassword(newPassword);
        }

        // Save changes
        userRepository.save(user);

        // Update session with latest user
        session.setAttribute("loggedInUser", user);

        model.addAttribute("user", user);
        model.addAttribute("success", "Profile updated successfully!");

        return "profile";
    }
}