package com.sintropia.calculator.service;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.AddressDTO;
import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.model.Coordinates;

@Service
public class CalculatorService {

    // Fonte: https://www.paymentsdive.com/news/payments-companies-look-towards-greener-future-roadblocks-lie-ahead/602847/
    private static final double PHYSICAL_CARD_WEIGHT_KG = 0.005;

    // Fonte: https://www.ecoinvent.org
    private static final double PVC_FACTOR = 2.7;

    // Fonte: https://www.gov.uk/government/publications/greenhouse-gas-reporting-conversion-factors-2023
    private static final double TRANSPORT_FACTOR = 0.21;

    // Fonte: https://www.gov.br/mcti/pt-br
    private static final double ENERGY_FACTOR = 0.084;

    // Fonte: https://www.terragreetings.com/lookout/carbon-footprint-digital-vs-physical-cards
    private static final double DIGITAL_CARD_EMISSION_KG = 0.01;

    // Fonte: https://www.dnb.nl/media/a3sk2oob/574-evaluating-the-environmental-impact-of-debit-card-payments.pdf
    private static final double EMISSION_PER_TRANSACTION_KG = 0.00085;

    // 1 transação por dia útil (~252 dias/ano)
    private static final int DEFAULT_ANNUAL_TRANSACTIONS_PER_STAFF = 264;

    // Custo de referência por cartão físico (produção + material)
    // Fonte: Nilson Report / mercado de emissão de cartões corporativos (~$2.00 USD)
    private static final double PHYSICAL_CARD_PRODUCTION_COST_USD = 2.00;

    // Custo médio de frete/envio por cartão (referência logística B2B ~$1.50 USD)
    private static final double PHYSICAL_CARD_SHIPPING_COST_USD = 1.50;

    private final AddressService addressService;

    public CalculatorService(AddressService addressService) {
        this.addressService = addressService;
    }

    public CalculationResponseDTO calculate(UserDTO user) throws Exception {
        Coordinates userCoords = addressService.getCoordinates(
            user.address().city(),
            user.address().state()
        );

        double distanceKm = addressService.getClosestFactoryDistance(userCoords);
        int cardCount = user.staffCount();

        double productionEmission   = calculateProductionEmission(cardCount);
        double transportEmission    = calculateTransportEmission(cardCount, distanceKm);
        double transactionEmission  = calculateTransactionEmission(cardCount);

        double annualPhysicEmission = (productionEmission + transportEmission) + transactionEmission;
        double annualDigitalEmission = (cardCount * DIGITAL_CARD_EMISSION_KG) + transactionEmission;

        double productionEmissionPercentage  = (annualPhysicEmission > 0) ? (productionEmission  / annualPhysicEmission) * 100.0 : 0.0;
        double transportEmissionPercentage   = (annualPhysicEmission > 0) ? (transportEmission   / annualPhysicEmission) * 100.0 : 0.0;
        double transactionEmissionPercentage = (annualPhysicEmission > 0) ? (transactionEmission / annualPhysicEmission) * 100.0 : 0.0;

        double money_wasted = cardCount * (PHYSICAL_CARD_PRODUCTION_COST_USD + PHYSICAL_CARD_SHIPPING_COST_USD);

        return new CalculationResponseDTO(
            annualPhysicEmission,
            annualDigitalEmission,
            money_wasted,
            transportEmissionPercentage,
            productionEmissionPercentage,
            transactionEmissionPercentage,
            cardCount
        );
    }
    
    public double calculateAnualPhysicEmission(long numberOfCards,AddressDTO address) throws Exception {
    	if(address == null) return 0.0d;
    	
    	Coordinates coordinates = addressService.getCoordinates(address.city(), address.state());
    	
    	double distanceKm = addressService.getClosestFactoryDistance(coordinates);
    	
    	double productionEmission = calculateProductionEmission(numberOfCards);
    	double transportEmission = calculateTransportEmission(numberOfCards, distanceKm);
    	double transactionEmission = calculateTransactionEmission(numberOfCards);
    	
    	return productionEmission + transportEmission + transactionEmission;
    }

    private double calculateProductionEmission(long numberOfCards) {
        if (numberOfCards <= 0) return 0.0;
        double pvcEmission    = PHYSICAL_CARD_WEIGHT_KG * PVC_FACTOR * numberOfCards;
        double energyEmission = ENERGY_FACTOR * numberOfCards;
        return pvcEmission + energyEmission;
    }

    private double calculateTransportEmission(long numberOfCards, double distanceKm) {
        if (numberOfCards <= 0) return 0.0;
        return (PHYSICAL_CARD_WEIGHT_KG * numberOfCards / 1000.0) * distanceKm * TRANSPORT_FACTOR;
    }

    private double calculateTransactionEmission(long numberOfCards) {
        if (numberOfCards <= 0) return 0.0;
        return EMISSION_PER_TRANSACTION_KG * DEFAULT_ANNUAL_TRANSACTIONS_PER_STAFF * numberOfCards;
    }
}