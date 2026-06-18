package com.sintropia.calculator.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.MonthlyRecordDTO;
import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.dto.response.SummaryResponseDTO;
import com.sintropia.calculator.model.GoalType;

@Service
public class SummaryService {

    private final CalculatorService calculatorService;

    public SummaryService(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    public SummaryResponseDTO getSummary(UserDTO user) throws Exception {
        CalculationResponseDTO calculation = calculatorService.calculate(user);

        long staffCount = user.staffCount();
        long digitalStaffCount = user.digitalCardStaffCount() != null ? user.digitalCardStaffCount() : 0;
        double qualityScore = staffCount > 0 ? (digitalStaffCount * 100.0) / staffCount : 0.0;

        List<MonthlyRecordDTO> records = user.monthlyRecords();
        LocalDate monitoringSince = (records == null || records.isEmpty()) ? null : records.get(0).recordDate();

        String summaryText = buildSummaryText(user, calculation, qualityScore, digitalStaffCount, monitoringSince);

        return new SummaryResponseDTO(
            user.name(),
            user.industrySector() != null ? user.industrySector().getLabel() : null,
            user.address() != null ? user.address().city() : null,
            user.address() != null ? user.address().state() : null,
            qualityScore,
            calculation.annualPhysicEmission(),
            calculation.annualDigitalEmission(),
            calculation.moneyWasted(),
            summaryText,
            monitoringSince
        );
    }

    public double getCurrentValueForGoalType(UserDTO user, GoalType type) throws Exception {
        switch (type) {
            case INDICE_MIGRACAO_DIGITAL -> {
                long staffCount = user.staffCount();
                long digitalStaffCount = user.digitalCardStaffCount() != null ? user.digitalCardStaffCount() : 0;
                return staffCount > 0 ? (digitalStaffCount * 100.0) / staffCount : 0.0;
            }
            case DINHEIRO_ECONOMIZADO -> {
                CalculationResponseDTO calculation = calculatorService.calculate(user);
                return calculation.moneyWasted();
            }
            case CO2_EVITADO_ACUMULADO -> {
                return getAccumulatedCo2Avoided(user);
            }
            default -> {
                return 0.0;
            }
        }
    }

    private double getAccumulatedCo2Avoided(UserDTO user) throws Exception {
        if (user.address() == null) return 0.0;

        double distanceKm = calculatorService.getDistanceForAddress(user.address());
        double avoidedPerCard = calculatorService.calculateAvoidedEmissionPerCard(distanceKm);

        List<MonthlyRecordDTO> records = user.monthlyRecords();
        if (records == null) return 0.0;

        return records.stream()
            .mapToDouble(r -> (r.digitalStaffCount() != null ? r.digitalStaffCount() : 0) * avoidedPerCard)
            .sum();
    }

    private String buildSummaryText(UserDTO user, CalculationResponseDTO calculation, double qualityScore,
                                     long digitalStaffCount, LocalDate monitoringSince) {
        double potentialReduction = calculation.annualPhysicEmission() - calculation.annualDigitalEmission();

        String since = monitoringSince != null
            ? monitoringSince.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", new Locale("pt", "BR")))
            : "o início do monitoramento";

        return String.format(Locale.forLanguageTag("pt-BR"),
            "Durante o período analisado, a %s apresentou um índice de migração digital de %.0f%% " +
            "(%d funcionários), o que gerou impactos positivos tanto na redução de emissões quanto nos custos " +
            "operacionais. Apesar dos avanços, os cartões físicos ainda emitem cerca de %.2f kg de CO2, " +
            "enquanto os digitais geram apenas %.2f kg. Essa diferença evidencia o potencial de redução de " +
            "%.2f kg de CO2 por ano com a ampliação da migração para o digital, monitorado desde %s.",
            user.name(), qualityScore, digitalStaffCount,
            calculation.annualPhysicEmission(), calculation.annualDigitalEmission(),
            potentialReduction, since
        );
    }
}