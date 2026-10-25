package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.Habit;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.HabitRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HabitController {

    private final HabitRepository habitRepository;

    public HabitController(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    // ================= HABIT PAGE =================

    @GetMapping("/habits")
    public String habits(Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("habits", habitRepository.findByUser(user));
        model.addAttribute("user", user);

        return "habits";
    }

    // ================= ADD HABIT =================

    @PostMapping("/habits/add")
    public String addHabit(
            @RequestParam String name,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Habit habit = new Habit();

        habit.setName(name);
        habit.setCompleted(false);
        habit.setStreak(0);
        habit.setUser(user);

        habitRepository.save(habit);

        return "redirect:/habits";
    }

    // ================= TOGGLE HABIT =================

    @PostMapping("/habits/toggle")
    public String toggleHabit(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Habit habit = habitRepository.findById(id).orElse(null);

        if (habit != null
                && habit.getUser() != null
                && habit.getUser().getId().equals(user.getId())) {

            if (habit.isCompleted()) {

                habit.setCompleted(false);

                if (habit.getStreak() > 0) {
                    habit.setStreak(habit.getStreak() - 1);
                }

            } else {

                habit.setCompleted(true);
                habit.setStreak(habit.getStreak() + 1);
            }

            habitRepository.save(habit);
        }

        return "redirect:/habits";
    }

    // ================= DELETE HABIT =================

    @PostMapping("/habits/delete")
    public String deleteHabit(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Habit habit = habitRepository.findById(id).orElse(null);

        if (habit != null
                && habit.getUser() != null
                && habit.getUser().getId().equals(user.getId())) {

            habitRepository.delete(habit);
        }

        return "redirect:/habits";
    }
}