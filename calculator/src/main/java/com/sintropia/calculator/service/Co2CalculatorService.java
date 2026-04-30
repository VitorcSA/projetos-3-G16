package com.sintropia.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Co2CalculatorService{
	
	@Value("${climatiq.api.key}")
	private String apiKey;
	
	@Value("${climatiq.api.url}")
	private String apiUrl;

	private static final float cardWeight = 5;

	public double calculateFisicalCardIssues(int employeesCount){
		double totalWeight = (employeesCount * cardWeight) / 1000.0d;

		ClimatiqRequest bodyRequest = new ClimatiqRequest(
			"plastics_rubber-type_average_plastics_film_primary_material_production",
			totalWeight,
			"kg"
		);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization","Bearer "+ apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ClimatiqRequest> requestEntity = new HttpEntity<ClimatiqRequest>(bodyRequest,headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ClimatiqResponse> response = restTemplate.postForEntity(
			apiUrl,
			requestEntity,
			ClimatiqResponse.class
		);


		if(response.getBody() != null){
			return response.getBody().getCo2e();
		}

		return 0.0;
	}


	public double calculateDigitalCardIssues(int employeesCount){
		return 0.0;
	}

}
