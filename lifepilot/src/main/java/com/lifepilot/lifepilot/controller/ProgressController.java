package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.Habit;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.TaskRepository;
import com.lifepilot.lifepilot.repository.GoalRepository;
import com.lifepilot.lifepilot.repository.HabitRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProgressController {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final HabitRepository habitRepository;

    public ProgressController(
            TaskRepository taskRepository,
            GoalRepository goalRepository,
            HabitRepository habitRepository) {

        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
        this.habitRepository = habitRepository;
    }

    @GetMapping("/progress")
    public String progress(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        // User login nahi hai
        if (user == null) {
            return "redirect:/login";
        }

        String email = user.getEmail();

        // ================= TASKS =================

        List<com.lifepilot.lifepilot.entity.Task> tasks = taskRepository.findByUserEmail(email);

        int totalTasks = tasks.size();
        int completedTasks = 0;

        for (com.lifepilot.lifepilot.entity.Task task : tasks) {

            if (task.isCompleted()) {
                completedTasks++;
            }
        }

        int pendingTasks = totalTasks - completedTasks;

        // ================= GOALS =================

        List<com.lifepilot.lifepilot.entity.Goal> goals = goalRepository.findByUserEmailOrderByIdDesc(email);

        int totalGoals = goals.size();
        int completedGoals = 0;

        for (com.lifepilot.lifepilot.entity.Goal goal : goals) {

            if (goal.isCompleted()) {
                completedGoals++;
            }
        }

        int pendingGoals = totalGoals - completedGoals;

        // ================= HABITS =================

        List<Habit> habits = habitRepository.findByUser(user);

        int totalHabits = habits.size();
        int completedHabits = 0;

        for (Habit habit : habits) {

            if (habit.isCompleted()) {
                completedHabits++;
            }
        }

        int pendingHabits = totalHabits - completedHabits;

        // ================= OVERALL PROGRESS =================

        int totalItems = totalTasks + totalGoals + totalHabits;

        int completedItems = completedTasks +
                completedGoals +
                completedHabits;

        int overallProgress = 0;

        if (totalItems > 0) {
            overallProgress = (completedItems * 100) / totalItems;
        }

        // ================= SEND DATA TO HTML =================

        model.addAttribute("user", user);

        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);

        model.addAttribute("totalGoals", totalGoals);
        model.addAttribute("completedGoals", completedGoals);
        model.addAttribute("pendingGoals", pendingGoals);

        model.addAttribute("totalHabits", totalHabits);
        model.addAttribute("completedHabits", completedHabits);
        model.addAttribute("pendingHabits", pendingHabits);

        model.addAttribute("totalItems", totalItems);
        model.addAttribute("completedItems", completedItems);
        model.addAttribute("overallProgress", overallProgress);

        return "progress";
    }
}