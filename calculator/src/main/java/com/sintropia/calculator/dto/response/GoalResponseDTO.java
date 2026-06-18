package com.sintropia.calculator.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record GoalResponseDTO(
    Long id,
    @JsonProperty("type") String type,
    @JsonProperty("type_label") String typeLabel,
    @JsonProperty("unit") String unit,
    @JsonProperty("target_value") double targetValue,
    @JsonProperty("current_value") double currentValue,
    @JsonProperty("target_date") LocalDate targetDate
) {}