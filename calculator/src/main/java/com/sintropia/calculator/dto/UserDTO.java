package com.sintropia.calculator.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDTO(
		String name,
		String email,
		@JsonProperty("staff_count") long staffCount,
		AddressDTO address,
		@JsonProperty("digital_card_staff_count") Long digitalCardStaffCount,
		@JsonProperty("monthly_records") List<MonthlyRecordDTO> monthlyRecords
	){}