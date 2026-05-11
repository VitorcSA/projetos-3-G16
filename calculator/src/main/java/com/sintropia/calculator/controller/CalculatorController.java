package com.sintropia.calculator.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.sintropia.calculator.service.Co2CalculatorService;

@RestController
@RequestMapping("api/calculator")
@CrossOrigin(origins = "*")
public class CalculatorController {
	
	private final Co2CalculatorService service;

	public CalculatorController(Co2CalculatorService service){
		this.service = service;
	}

	@PostMapping("/compare")
	public CompareResponse compareIssues(@RequestBody EmployeeRequest request){
		
		double fisicalIssues = service.calculateFisicalCardIssues(request.employees());
		double digitalIssues = service.calculateDigitalCardIssues(request.employees());

		double economy = fisicalIssues - digitalIssues;

		return new CompareResponse(
			request.employees(),
			fisicalIssues,
			digitalIssues,
			economy
		);
	}

	public record EmployeeRequest(int employees) {}

	public record CompareResponse(
			int employees,
			double fisicalEmission,
			double digitalEmission,
			double economy
	) {}
}