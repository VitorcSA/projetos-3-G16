package com.sintropia.calculator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sintropia.calculator.model.Goal;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdOrderByTargetDateAsc(Long userId);
}