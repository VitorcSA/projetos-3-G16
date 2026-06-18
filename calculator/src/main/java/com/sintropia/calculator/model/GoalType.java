package com.sintropia.calculator.model;

public enum GoalType {
    CO2_EVITADO_ACUMULADO("CO2 Evitado Acumulado", "Kg"),
    INDICE_MIGRACAO_DIGITAL("Índice de Migração Digital", "%"),
    DINHEIRO_ECONOMIZADO("Dinheiro Economizado", "R$");

    private final String label;
    private final String unit;

    GoalType(String label, String unit) {
        this.label = label;
        this.unit = unit;
    }

    public String getLabel() {
        return label;
    }

    public String getUnit() {
        return unit;
    }
}