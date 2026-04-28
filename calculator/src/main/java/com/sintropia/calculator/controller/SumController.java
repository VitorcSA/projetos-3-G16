package com.sintropia.calculator.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class SumController {
	
	static public class sumRequest{
		public int num1;
		public int num2;
	}

	@PostMapping("/sum")
	public String sum(@RequestBody sumRequest data) {
		int result = data.num1 + data.num2;
		return "o resultado da some é: " + result;
	}
}
