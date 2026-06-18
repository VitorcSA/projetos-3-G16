package com.sintropia.calculator.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;


public record MonthlyRecordDTO(
        @JsonProperty("record_date") LocalDate recordDate,
        @JsonProperty("staff_count") Long staffCount,
        @JsonProperty("digital_staff_count") Long digitalStaffCount
) {}
