package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.Task;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.TaskRepository;
import com.lifepilot.lifepilot.repository.GoalRepository;
import com.lifepilot.lifepilot.repository.HabitRepository;
import com.lifepilot.lifepilot.repository.NoteRepository;
import com.lifepilot.lifepilot.repository.ExpenseRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final HabitRepository habitRepository;
    private final NoteRepository noteRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardController(
            TaskRepository taskRepository,
            GoalRepository goalRepository,
            HabitRepository habitRepository,
            NoteRepository noteRepository,
            ExpenseRepository expenseRepository) {

        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
        this.habitRepository = habitRepository;
        this.noteRepository = noteRepository;
        this.expenseRepository = expenseRepository;
    }

    // =========================
    // DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        // =========================
        // CHECK LOGIN
        // =========================

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        String email = user.getEmail();

        // =========================
        // TASKS
        // =========================

        List<Task> allTasks =
                taskRepository.findByUserEmail(email);

        LocalDate today = LocalDate.now();

        List<Task> todayTasks =
                taskRepository.findByUserEmailAndTaskDate(
                        email,
                        today);

        int totalTasks = allTasks.size();

        int completedTasks = 0;

        for (Task task : allTasks) {
            if (task.isCompleted()) {
                completedTasks++;
            }
        }

        int pendingTasks = totalTasks - completedTasks;


        // =========================
        // GOALS
        // =========================

        var goals =
                goalRepository.findByUserEmailOrderByIdDesc(email);

        int totalGoals = goals.size();

        int completedGoals = 0;

        for (var goal : goals) {
            if (goal.isCompleted()) {
                completedGoals++;
            }
        }


        // =========================
        // HABITS
        // =========================

        var habits =
                habitRepository.findByUser(user);

        int totalHabits = habits.size();

        int completedHabits = 0;

        for (var habit : habits) {
            if (habit.isCompleted()) {
                completedHabits++;
            }
        }


        // =========================
        // NOTES
        // =========================

        var notes =
                noteRepository.findByUserEmailOrderByPinnedDescIdDesc(
                        email);

        int totalNotes = notes.size();


        // =========================
        // EXPENSES
        // =========================

        var expenses =
                expenseRepository
                        .findByUserEmailOrderByExpenseDateDescIdDesc(
                                email);

        double totalExpense = 0;

        for (var expense : expenses) {
            totalExpense += expense.getAmount();
        }


        // =========================
        // USER
        // =========================

        model.addAttribute("user", user);


        // =========================
        // TASK DATA
        // =========================

        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("todayTasks", todayTasks);


        // =========================
        // GOAL DATA
        // =========================

        model.addAttribute("totalGoals", totalGoals);
        model.addAttribute("completedGoals", completedGoals);


        // =========================
        // HABIT DATA
        // =========================

        model.addAttribute("totalHabits", totalHabits);
        model.addAttribute("completedHabits", completedHabits);


        // =========================
        // NOTE DATA
        // =========================

        model.addAttribute("totalNotes", totalNotes);


        // =========================
        // EXPENSE DATA
        // =========================

        model.addAttribute("totalExpense", totalExpense);


        // =========================
        // DATE
        // =========================

        model.addAttribute("today", today);

        return "dashboard";
    }
}