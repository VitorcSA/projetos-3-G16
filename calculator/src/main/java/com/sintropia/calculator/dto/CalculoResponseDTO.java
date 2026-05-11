package com.sintropia.calculator.dto;

public class CalculoResponseDTO {

    private double emissaoCartaoFisico;
    private double emissaoCartaoDigital;
    private double diferenca;
    private double percentualReducao;

    public double getEmissaoCartaoFisico() {
        return emissaoCartaoFisico;
    }

    public void setEmissaoCartaoFisico(double emissaoCartaoFisico) {
        this.emissaoCartaoFisico = emissaoCartaoFisico;
    }

    public double getEmissaoCartaoDigital() {
        return emissaoCartaoDigital;
    }

    public void setEmissaoCartaoDigital(double emissaoCartaoDigital) {
        this.emissaoCartaoDigital = emissaoCartaoDigital;
    }

    public double getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(double diferenca) {
        this.diferenca = diferenca;
    }

    public double getPercentualReducao() {
        return percentualReducao;
    }

    public void setPercentualReducao(double percentualReducao) {
        this.percentualReducao = percentualReducao;
    }
}