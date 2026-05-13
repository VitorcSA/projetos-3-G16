package com.sintropia.calculator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Calculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double pesoPvc;
    private double distanciaTransporteKm;
    private double consumoEnergiaKwh;

    public Long getId() {
        return id;
    }

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