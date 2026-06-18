package com.sintropia.calculator.model;

public enum IndustrySector {
    VAREJO("Varejo"),
    SAUDE("Saúde"),
    TECNOLOGIA("Tecnologia"),
    INDUSTRIA("Indústria"),
    FINANCEIRO("Financeiro"),
    EDUCACAO("Educação"),
    SERVICOS("Serviços"),
    OUTRO("Outro");

    private final String label;

    IndustrySector(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}