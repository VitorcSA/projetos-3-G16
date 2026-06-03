package com.sintropia.calculator.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CalculationResponseDTO(
		@JsonProperty("physical_card_emission") double physicalCardEmission,
		@JsonProperty("digital_card_emission") double digitalCardEmission,
		double difference,
		@JsonProperty("reduction_percentage") double reductionPercentage) {}
