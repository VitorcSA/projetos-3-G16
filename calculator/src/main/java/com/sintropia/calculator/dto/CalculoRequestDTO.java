package com.sintropia.calculator.dto;

public class CalculoRequestDTO {

    private double pesoPvc;
    private double distanciaTransporteKm;
    private double consumoEnergiaKwh;

    public double getPesoPvc() {
        return pesoPvc;
    }

    public void setPesoPvc(double pesoPvc) {
        this.pesoPvc = pesoPvc;
    }

    public double getDistanciaTransporteKm() {
        return distanciaTransporteKm;
    }

    public void setDistanciaTransporteKm(double distanciaTransporteKm) {
        this.distanciaTransporteKm = distanciaTransporteKm;
    }

    public double getConsumoEnergiaKwh() {
        return consumoEnergiaKwh;
    }

    public void setConsumoEnergiaKwh(double consumoEnergiaKwh) {
        this.consumoEnergiaKwh = consumoEnergiaKwh;
    }
}