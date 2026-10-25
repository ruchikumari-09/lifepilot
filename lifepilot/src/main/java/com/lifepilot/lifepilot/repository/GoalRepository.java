package com.lifepilot.lifepilot.repository;

import com.lifepilot.lifepilot.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserEmailOrderByIdDesc(String userEmail);

}