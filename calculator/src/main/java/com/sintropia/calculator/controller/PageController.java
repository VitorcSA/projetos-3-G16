package com.sintropia.calculator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PageController{

	@GetMapping("/login")
	public String login(){
		return "forward:/login.html";
	}

	@GetMapping("/")
	public String home() {
		return "forward:/index.html";
	}
}
