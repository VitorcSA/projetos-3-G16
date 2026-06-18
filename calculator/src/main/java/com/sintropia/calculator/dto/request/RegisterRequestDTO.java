package com.sintropia.calculator.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sintropia.calculator.dto.AddressDTO;
import com.sintropia.calculator.model.IndustrySector;

public record RegisterRequestDTO(
		String name,
		String email,
		String password,
		@JsonProperty("staff_count") Long staffCount,
		AddressDTO address,
		@JsonProperty("digital_card_staff_count") Long digitalCardStaffCount,
		@JsonProperty("industry_sector") IndustrySector industrySector) {
}