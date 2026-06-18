package com.sintropia.calculator.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record SummaryResponseDTO(
    @JsonProperty("company_name") String companyName,
    @JsonProperty("industry_sector") String industrySector,
    @JsonProperty("city") String city,
    @JsonProperty("state") String state,
    @JsonProperty("quality_score") double qualityScore,

    @JsonProperty("physical_card_emission") double physicalCardEmission,
    @JsonProperty("digital_card_emission") double digitalCardEmission,
    @JsonProperty("money_wasted") double moneyWasted,

    @JsonProperty("summary") String summary,

    @JsonProperty("monitoring_since") LocalDate monitoringSince
) {}