package com.sintropia.calculator.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.service.CalculatorService;
import com.sintropia.calculator.service.GoalsService;
import com.sintropia.calculator.service.UserService;


@Controller
public class PrivatePageController{

	private final UserService userService;
	private final GoalsService goalsService;
	private final CalculatorService calculatorService;
	
	
	public PrivatePageController(UserService userService,GoalsService goalsService,CalculatorService calculatorService) {
		this.userService = userService;
		this.goalsService = goalsService;
		this.calculatorService = calculatorService;
	}
	
    @ModelAttribute("user")
    public UserDTO currentUser(@AuthenticationPrincipal String email) {
        return userService.findByEmail(email);
    }
	
    @GetMapping("/")
    public String home(@ModelAttribute("user") UserDTO user, Model model) throws Exception {
        CalculationResponseDTO data = calculatorService.calculate(user);
        model.addAttribute("calculation", data);
        return "index";
    }
	
	@GetMapping("/history")
	public String history(@ModelAttribute("user") UserDTO user, Model model) throws Exception {
		double emission = 0.0d;
		
		if(user.digitalCardStaffCount() != null)
			emission = calculatorService.calculateAnualPhysicEmission(user.digitalCardStaffCount(),user.address());
		
		model.addAttribute("emission", emission);
		model.addAttribute("monthly_records",user.monthlyRecords());
		model.addAttribute("goals",goalsService.calculateGoals(user));

		return "history";
	}
	
}
