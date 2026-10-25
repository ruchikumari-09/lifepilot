package com.lifepilot.lifepilot.controller;

import com.lifepilot.lifepilot.entity.Expense;
import com.lifepilot.lifepilot.entity.User;
import com.lifepilot.lifepilot.repository.ExpenseRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // ================= EXPENSE PAGE =================

    @GetMapping("/expenses")
    public String expenses(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        List<Expense> expenses = expenseRepository.findByUserEmailOrderByExpenseDateDescIdDesc(
                user.getEmail());

        double totalExpense = 0;
        double monthlyExpense = 0;

        LocalDate today = LocalDate.now();

        Map<String, Double> categoryTotals = new LinkedHashMap<>();

        for (Expense expense : expenses) {

            // Total expense
            totalExpense += expense.getAmount();

            // Current month expense
            if (expense.getExpenseDate() != null
                    && expense.getExpenseDate().getMonth() == today.getMonth()
                    && expense.getExpenseDate().getYear() == today.getYear()) {

                monthlyExpense += expense.getAmount();
            }

            // Category-wise total
            String category = expense.getCategory();

            if (category != null && !category.isBlank()) {

                categoryTotals.put(
                        category,
                        categoryTotals.getOrDefault(category, 0.0)
                                + expense.getAmount());
            }
        }

        model.addAttribute("expenses", expenses);

        model.addAttribute("totalExpense", totalExpense);

        model.addAttribute("monthlyExpense", monthlyExpense);

        model.addAttribute("categoryTotals", categoryTotals);

        model.addAttribute("today", today);

        return "expenses";
    }

    // ================= ADD EXPENSE =================

    @PostMapping("/expenses/add")
    public String addExpense(
            @RequestParam double amount,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam LocalDate expenseDate,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Expense expense = new Expense();

        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setExpenseDate(expenseDate);
        expense.setUserEmail(user.getEmail());

        expenseRepository.save(expense);

        return "redirect:/expenses";
    }

    // ================= EDIT EXPENSE =================

    @GetMapping("/expenses/edit")
    public String editExpense(
            @RequestParam Long id,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null ||
                !expense.getUserEmail().equals(user.getEmail())) {

            return "redirect:/expenses";
        }

        model.addAttribute("expense", expense);

        return "edit-expense";
    }

    // ================= UPDATE EXPENSE =================

    @PostMapping("/expenses/update")
    public String updateExpense(
            @RequestParam Long id,
            @RequestParam double amount,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam LocalDate expenseDate,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense != null &&
                expense.getUserEmail().equals(user.getEmail())) {

            expense.setAmount(amount);
            expense.setCategory(category);
            expense.setDescription(description);
            expense.setExpenseDate(expenseDate);

            expenseRepository.save(expense);
        }

        return "redirect:/expenses";
    }

    // ================= DELETE EXPENSE =================

    @PostMapping("/expenses/delete")
    public String deleteExpense(
            @RequestParam Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense != null &&
                expense.getUserEmail().equals(user.getEmail())) {

            expenseRepository.delete(expense);
        }

        return "redirect:/expenses";
    }
}