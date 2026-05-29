package com.sintropia.calculator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController{

	@GetMapping("/login")
	public String login(){
		return "login";
	}

	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	@GetMapping("/register")
	public String register(){
		return "register";
	}
	
	@GetMapping("/edit")
	public String edit(){
		return "edit";
	}
	
	@GetMapping("/profile")
	public String profile() {
		return "profile";
	}
	
	@GetMapping("/calculator")
	public String calculator() {
		return "calculator";
	}
	
}
