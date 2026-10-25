package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.Goal;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.GoalRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class GoalController {

    private final GoalRepository goalRepository;

    public GoalController(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // =========================
    // GOALS PAGE
    // =========================

    @GetMapping("/goals")
    public String goals(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        List<Goal> goals = goalRepository.findByUserEmailOrderByIdDesc(
                user.getEmail());

        model.addAttribute("goals", goals);
        model.addAttribute("today", LocalDate.now());

        return "goals";
    }

    // =========================
    // ADD GOAL
    // =========================

    @PostMapping("/goals/add")
    public String addGoal(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) LocalDate targetDate,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Goal goal = new Goal();

        goal.setTitle(title);
        goal.setDescription(description);
        goal.setTargetDate(targetDate);
        goal.setProgress(0);
        goal.setCompleted(false);
        goal.setUserEmail(user.getEmail());

        goalRepository.save(goal);

        return "redirect:/goals";
    }

    // =========================
    // UPDATE PROGRESS
    // =========================

    @PostMapping("/goals/progress")
    public String updateProgress(
            @RequestParam Long id,
            @RequestParam int progress,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Goal goal = goalRepository.findById(id).orElse(null);

        if (goal != null &&
                goal.getUserEmail().equals(user.getEmail())) {

            if (progress < 0) {
                progress = 0;
            }

            if (progress > 100) {
                progress = 100;
            }

            goal.setProgress(progress);

            if (progress == 100) {
                goal.setCompleted(true);
            } else {
                goal.setCompleted(false);
            }

            goalRepository.save(goal);
        }

        return "redirect:/goals";
    }

    // =========================
    // COMPLETE / UNCOMPLETE
    // =========================

    @GetMapping("/goals/toggle")
    public String toggleGoal(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Goal goal = goalRepository.findById(id).orElse(null);

        if (goal != null &&
                goal.getUserEmail().equals(user.getEmail())) {

            if (goal.isCompleted()) {
                goal.setCompleted(false);

                if (goal.getProgress() == 100) {
                    goal.setProgress(0);
                }

            } else {
                goal.setCompleted(true);
                goal.setProgress(100);
            }

            goalRepository.save(goal);
        }

        return "redirect:/goals";
    }

    // =========================
    // DELETE GOAL
    // =========================

    @GetMapping("/goals/delete")
    public String deleteGoal(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Goal goal = goalRepository.findById(id).orElse(null);

        if (goal != null &&
                goal.getUserEmail().equals(user.getEmail())) {

            goalRepository.delete(goal);
        }

        return "redirect:/goals";
    }
}