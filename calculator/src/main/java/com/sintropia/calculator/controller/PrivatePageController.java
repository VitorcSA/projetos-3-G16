package com.sintropia.calculator.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.sintropia.calculator.dto.response.CalculationResponseDTO;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.CalculatorService;
import com.sintropia.calculator.service.UserService;


@Controller
public class PrivatePageController{

	private final UserService userService;
	private final CalculatorService calculatorService;
	
	public PrivatePageController(UserService userService,CalculatorService calculatorService) {
		this.userService = userService;
		this.calculatorService = calculatorService;
	}
	
    @ModelAttribute("user")
    public User currentUser(@AuthenticationPrincipal String email) {
        return userService.findByEmail(email);
    }
	
    @GetMapping("/")
    public String home(@ModelAttribute("user") User user, Model model) throws Exception {
        CalculationResponseDTO data = calculatorService.calculate(user);
        model.addAttribute("calculation", data);
        return "index";
    }
	
	@GetMapping("/history")
	public String history() {
		return "history";
	}
	
}
