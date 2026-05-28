package com.sintropia.calculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.dto.request.RegisterRequestDTO;
import com.sintropia.calculator.model.Address;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.UserService;


@RestController
@RequestMapping("api/user")
public class UserController{

	private final UserService service;

	public UserController(UserService service){
		this.service = service;
	}

	@PostMapping("/register") 
	public ResponseEntity<String> registerController(@RequestBody RegisterRequestDTO request){
		try{
			Address address = new Address(
				request.address().street(),
				request.address().number(),
				request.address().city(),
				request.address().state(),
				request.address().zipCode()
			);

			User newUser = new User(request.name(),request.email(),request.password(),request.staffCount(),address);
			service.register(newUser);

			return ResponseEntity.ok("sucess");
		}catch (IllegalArgumentException error){
			return ResponseEntity.badRequest().body(error.getMessage());
		}
		
	}

}
