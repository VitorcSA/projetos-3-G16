package com.sintropia.calculator.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.request.CalculoRequestDTO;
import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.dto.response.CalculoResponseDTO;
import com.sintropia.calculator.model.Calculo;
import com.sintropia.calculator.model.Coordinates;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.repository.CalculoRepository;

@Service
public class CalculatorService {

	//Fonte: https://www.paymentsdive.com/news/payments-companies-look-towards-greener-future-roadblocks-lie-ahead/602847/
	private static final double PHYSICAL_CARD_WEIGHT_GRAMS = 5.0;
	//Fonte: https://www.ecoinvent.org
	private static final double PVC_FACTOR = 2.7;
	//Fonte: https://www.gov.uk/government/publications/greenhouse-gas-reporting-conversion-factors-2023
	private static final double TRANSPORT_FACTOR = 0.21;
	//Fonte: https://www.gov.br/mcti/pt-br)
	private static final double ENERGY_FACTOR = 0.084;
	//Fonte: https://www.edgardunn.com/articles/how-to-mitigate-the-environmental-cost-of-payment-cards
	private static final double PHYSICAL_CARD_EMISSION_KG = 0.150;
	//Fonte: https://www.terragreetings.com/lookout/carbon-footprint-digital-vs-physical-cards
	private static final double DIGITAL_CARD_EMISSION_KG = 0.010;
	
	//Exemplo
	private static final int BATCH_SIZE = 1000;
	
	// De onde vieram esses dados ????? 
    private static final double FATOR_PVC = 2.7; //Com base em que???
    private static final double FATOR_TRANSPORTE = 0.21;
    private static final double FATOR_ENERGIA = 0.084;

    private final CalculoRepository repository;
    private final UserService userService;
    private final AddressService addressService;

    public CalculatorService(CalculoRepository repository,UserService userService,AddressService addressService) {
        this.repository = repository;
        this.userService = userService;
        this.addressService = addressService;
    }

    private CalculoRequestDTO buscarDadosDoBanco() {
        Calculo calculo = repository.findTopByOrderByIdDesc();
        
        CalculoRequestDTO dto = new CalculoRequestDTO();

        if (calculo == null) {
            dto.setPesoPvc(1000.0); //De onde veio esse dado??? 1.000 oq????
            dto.setDistanciaTransporteKm(250.0); //Como assim?? a distancia vai ser sempre 250 ????
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
    
    public CalculationResponseDTO calculate(User user) throws Exception {

        Coordinates userCoords = addressService.getCoordinates(
            user.getAddress().getCity(),
            user.getAddress().getState()
        );

        double distanceKm = addressService.getClosestFactoryDistance(userCoords);

        int staffCount = user.getStaffCount();
        double digitalPercentage = user.getDigitalPercentage() != null ? user.getDigitalPercentage() : 0.0;

        int digitalCards = (int) (staffCount * (digitalPercentage / 100));
        int physicalCards = staffCount - digitalCards;

        double pvcEmission = (PHYSICAL_CARD_WEIGHT_GRAMS / 1000) * PVC_FACTOR * physicalCards;
        double energyEmission = ENERGY_FACTOR * physicalCards;
        double transportEmission = (PHYSICAL_CARD_WEIGHT_GRAMS / 1000 * physicalCards / 1000) * distanceKm * TRANSPORT_FACTOR;
        double totalPhysicalEmission = pvcEmission + energyEmission + transportEmission;

        double digitalCardEmissionPerCard = DIGITAL_CARD_EMISSION_KG;
        double totalDigitalEmission = digitalCardEmissionPerCard * digitalCards;

        double difference = totalPhysicalEmission - totalDigitalEmission;
        double reductionPercentage = (difference / totalPhysicalEmission) * 100;

        return new CalculationResponseDTO(
            totalPhysicalEmission,
            totalDigitalEmission,
            digitalCardEmissionPerCard,
            difference,
            reductionPercentage,
            physicalCards
        );
    }
    
}