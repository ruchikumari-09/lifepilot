package com.lifepilot.lifepilot.repository;

import com.lifepilot.lifepilot.entity.Habit;
import com.lifepilot.lifepilot.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByUserEmail(String userEmail);

    List<Habit> findByUser(User user);
}