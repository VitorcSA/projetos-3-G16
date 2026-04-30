package com.sintropia.calculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("api/user")
public class UserController{

	private final UserService service;

	public UserController(UserService service){
		this.service = service;
	}

	//nome
	//email
	//senha
	@PostMapping("/register") 
	public ResponseEntity<String> registerController(@RequestBody RegisterRequestDTO request){
		try{
			User newUser = new User(request.name(),request.email(),request.password());
			service.register(newUser);

			return ResponseEntity.ok("sucess");
		}catch (IllegalArgumentException error){
			return ResponseEntity.badRequest().body(error.getMessage());
		}
		
	}

	record RegisterRequestDTO(String name,String email,String password){}

}
