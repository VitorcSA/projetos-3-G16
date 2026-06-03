package com.sintropia.calculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.CalculatorService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("/api/calculator")
@CrossOrigin(origins = "*")
public class CalculatorController {

    private final CalculatorService calculatorService;
    private final UserService userService;
    
    public CalculatorController(CalculatorService calculatorService,UserService userService) {
        this.calculatorService = calculatorService;
        this.userService = userService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculate(@AuthenticationPrincipal String email) throws Exception{
    	User user = userService.findByEmail(email);
    	return ResponseEntity.ok(calculatorService.calculate(user));
    }

}