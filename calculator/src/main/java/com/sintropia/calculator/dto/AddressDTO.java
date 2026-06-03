package com.sintropia.calculator.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
		@NotBlank(message = "Cidade é obrigatorio") String city,
		@NotBlank(message = "Estado é obrigatorio") String state
) {}
