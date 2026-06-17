package com.sintropia.calculator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDTO(
		String name,
		String email,
		@JsonProperty("staff_count") int staffCount,
		AddressDTO address,
		@JsonProperty("digital_card_staff_count") Long digitalCardStaffCount){}