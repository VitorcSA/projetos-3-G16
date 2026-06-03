package com.sintropia.calculator.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sintropia.calculator.dto.AddressDTO;

public record UserProfileDTO(
		String name,
		String email,
		@JsonProperty("staff_count") Integer staffCount,
		AddressDTO address,
		@JsonProperty("digital_card_staff_count") Long digitalCardStaffCount){}
