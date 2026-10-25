package com.lifepilot.lifepilot.repository;

import com.lifepilot.lifepilot.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // User ke saare expenses
    List<Expense> findByUserEmailOrderByExpenseDateDescIdDesc(String userEmail);

    // Kisi particular date ke expenses
    List<Expense> findByUserEmailAndExpenseDate(
            String userEmail,
            LocalDate expenseDate);
}