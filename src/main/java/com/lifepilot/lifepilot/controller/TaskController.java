package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.Task;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.TaskRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // =========================
    // DAILY PLANNER PAGE
    // =========================

    @GetMapping("/planner")
    public String planner(
            @RequestParam(required = false) String date,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // If no date is selected, use today's date
        LocalDate selectedDate;

        if (date == null || date.isEmpty()) {
            selectedDate = LocalDate.now();
        } else {
            selectedDate = LocalDate.parse(date);
        }

        // Tasks for selected date
        List<Task> tasks = taskRepository.findByUserEmailAndTaskDate(
                user.getEmail(),
                selectedDate);

        // All pending tasks with date and time
        List<Task> pendingTasks = taskRepository
                .findByUserEmailAndCompletedFalseOrderByTaskDateAscTaskTimeAsc(
                        user.getEmail());

        model.addAttribute("tasks", tasks);

        model.addAttribute("pendingTasks", pendingTasks);

        model.addAttribute("today", selectedDate);

        return "planner";
    }

    // =========================
    // ADD TASK
    // =========================

    @PostMapping("/planner/add")
    public String addTask(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam String taskDate,
            @RequestParam(required = false) String taskTime,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Task task = new Task();

        task.setTitle(title);
        task.setDescription(description);

        task.setTaskDate(LocalDate.parse(taskDate));

        if (taskTime != null && !taskTime.isEmpty()) {
            task.setTaskTime(LocalTime.parse(taskTime));
        }

        task.setCompleted(false);

        task.setUserEmail(user.getEmail());

        taskRepository.save(task);

        return "redirect:/planner?date=" + taskDate;
    }

    // =========================
    // COMPLETE / UNCOMPLETE TASK
    // =========================

    @GetMapping("/planner/toggle")
    public String toggleTask(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Task task = taskRepository.findById(id).orElse(null);

        if (task != null &&
                task.getUserEmail().equals(user.getEmail())) {

            task.setCompleted(!task.isCompleted());

            taskRepository.save(task);
        }

        if (task != null) {
            return "redirect:/planner?date=" + task.getTaskDate();
        }

        return "redirect:/planner";
    }

    // =========================
    // DELETE TASK
    // =========================

    @GetMapping("/planner/delete")
    public String deleteTask(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Task task = taskRepository.findById(id).orElse(null);

        if (task != null &&
                task.getUserEmail().equals(user.getEmail())) {

            LocalDate taskDate = task.getTaskDate();

            taskRepository.delete(task);

            return "redirect:/planner?date=" + taskDate;
        }

        return "redirect:/planner";
    }
}