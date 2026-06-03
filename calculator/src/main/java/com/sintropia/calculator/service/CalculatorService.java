package com.sintropia.calculator.service;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.model.Coordinates;
import com.sintropia.calculator.model.User;

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
	
	//Exemplo(sem fontes)
	private static final int BATCH_SIZE = 1000;

    private final AddressService addressService;

    public CalculatorService(AddressService addressService) {
        this.addressService = addressService;
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