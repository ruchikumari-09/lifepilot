package com.lifepilot.lifepilot.repository;

import com.lifepilot.lifepilot.entity.Task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // User ke saare tasks
    List<Task> findByUserEmail(String userEmail);

    // User ke kisi particular date ke tasks
    List<Task> findByUserEmailAndTaskDate(
            String userEmail,
            LocalDate taskDate);

    // User ke saare pending tasks, date aur time ke order me
    List<Task> findByUserEmailAndCompletedFalseOrderByTaskDateAscTaskTimeAsc(
            String userEmail);
}