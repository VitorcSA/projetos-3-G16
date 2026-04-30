package com.sintropia.calculator.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.service.Co2CalculatorService;

@RestController
@RequestMapping("api/calculator")
public class CalculatorController{
	
	private final Co2CalculatorService service;

	public CalculatorController(Co2CalculatorService service){
		this.service = service;
	}

	@PostMapping("/compare")
	public String compareIssues(@RequestBody EmployeeRequest request){
		
		double fisicalIssues = service.calculateFisicalCardIssues(request.employees());
		double digitalIssues = service.calculateDigitalCardIssues(request.employees());

		double economy = fisicalIssues - digitalIssues;
		return String.format(
			"Para %d funcionários: \n" +
			"Cartão Físico emitiria: %.2f kg de CO2.\n" +
			"Cartão Digital emitiria: %.2f kg de CO2.\n" +
			"Economia gerada: %.2f kg de CO2 poupados!", 
			request.employees(), fisicalIssues, digitalIssues, economy
        	);

	}

	public record EmployeeRequest(int employees) {
	}

}
