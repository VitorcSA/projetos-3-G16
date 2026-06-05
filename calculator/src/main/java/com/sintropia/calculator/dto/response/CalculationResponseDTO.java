package com.sintropia.calculator.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CalculationResponseDTO(
		@JsonProperty("annual_physical_emission") double annualPhysicEmission,
		@JsonProperty("annual_digital_emission") double annualDigitalEmission,
	    @JsonProperty("money_wasted") double moneyWasted,
	    
	    @JsonProperty("transport_emission_percentage") double transportEmissionPercentage,
	    @JsonProperty("production_emission_percentage") double productionEmissionPercentage,
	    @JsonProperty("disposal_emission_percentage") double disposalEmissionPercentage,
	    
	    @JsonProperty("card_count") int cardCount
) {}
