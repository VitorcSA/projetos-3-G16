package com.sintropia.calculator.service;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.CalculoRequestDTO;
import com.sintropia.calculator.dto.CalculoResponseDTO;
import com.sintropia.calculator.model.Calculo;
import com.sintropia.calculator.repository.CalculoRepository;

@Service
public class CalculadoraService {

    private static final double FATOR_PVC = 2.7;
    private static final double FATOR_TRANSPORTE = 0.21;
    private static final double FATOR_ENERGIA = 0.084;

    private final CalculoRepository repository;

    public CalculadoraService(CalculoRepository repository) {
        this.repository = repository;
    }

    public CalculoRequestDTO buscarDadosDoBanco() {
        Calculo calculo = repository.findTopByOrderByIdDesc();

        CalculoRequestDTO dto = new CalculoRequestDTO();

        if (calculo == null) {
            dto.setPesoPvc(1000.0);
            dto.setDistanciaTransporteKm(250.0);
            dto.setConsumoEnergiaKwh(10.0);
            return dto;
        }

        dto.setPesoPvc(calculo.getPesoPvc());
        dto.setDistanciaTransporteKm(calculo.getDistanciaTransporteKm());
        dto.setConsumoEnergiaKwh(calculo.getConsumoEnergiaKwh());

        return dto;
    }

    public CalculoResponseDTO calcular(CalculoRequestDTO request) {
        CalculoRequestDTO dados = buscarDadosDoBanco();

        System.out.println("Peso PVC: " + dados.getPesoPvc());
        System.out.println("Distância (km): " + dados.getDistanciaTransporteKm());
        System.out.println("Energia (kWh): " + dados.getConsumoEnergiaKwh());

        double pesoPvc = Math.max(dados.getPesoPvc(), 0.0);
        double distanciaKm = Math.max(dados.getDistanciaTransporteKm(), 0.0);
        double energiaKwh = Math.max(dados.getConsumoEnergiaKwh(), 0.0);

        double emissaoFisico =
                ((pesoPvc / 1000.0) * FATOR_PVC) +
                (distanciaKm * FATOR_TRANSPORTE);

        double emissaoDigital =
                energiaKwh * FATOR_ENERGIA;

        double diferenca = emissaoFisico - emissaoDigital;

        double percentualReducao = 0.0;

        if (emissaoFisico > 0) {
            percentualReducao = (diferenca / emissaoFisico) * 100.0;
        }

        System.out.println("Emissão Física: " + emissaoFisico);
        System.out.println("Emissão Digital: " + emissaoDigital);
        System.out.println("Diferença: " + diferenca);
        System.out.println("Percentual de Redução: " + percentualReducao);

        CalculoResponseDTO response = new CalculoResponseDTO();
        response.setEmissaoCartaoFisico(emissaoFisico);
        response.setEmissaoCartaoDigital(emissaoDigital);
        response.setDiferenca(diferenca);
        response.setPercentualReducao(percentualReducao);

        return response;
    }
}