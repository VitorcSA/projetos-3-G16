package com.sintropia.calculator.dto.request;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sintropia.calculator.model.GoalType;

public record GoalRequestDTO(
    GoalType type,
    @JsonProperty("target_value") Double targetValue,
    @JsonProperty("target_date") LocalDate targetDate
) {}