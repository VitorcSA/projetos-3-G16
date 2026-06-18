package com.sintropia.calculator.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType type;

    @Column(name = "target_value", nullable = false)
    private double targetValue;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Goal() {}

    public Goal(GoalType type, double targetValue, LocalDate targetDate, User user) {
        this.type = type;
        this.targetValue = targetValue;
        this.targetDate = targetDate;
        this.user = user;
    }

    public Long getId() { return id; }

    public GoalType getType() { return type; }
    public void setType(GoalType type) { this.type = type; }

    public double getTargetValue() { return targetValue; }
    public void setTargetValue(double targetValue) { this.targetValue = targetValue; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}