package com.sintropia.calculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.JwtService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthController{

	private final UserService userService;
	private final JwtService jwtService;
	private final BCryptPasswordEncoder passwordEncoder;

	public AuthController(UserService userService, JwtService jwtService, BCryptPasswordEncoder passwordEncoder){
		this.userService = userService;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginDTO request){
		User user = userService.findByEmail(request.email());

		if(user == null){
			return ResponseEntity.status(401).body("Email ou senha invalidos");
		}

		if(!passwordEncoder.matches(request.password(),user.getPassword())){
			return ResponseEntity.status(401).body("Email ou senha invalidos");
		}

		String token = jwtService.generateToken(user.getEmail());
		return ResponseEntity.ok(token);
	}

	record LoginDTO(String email,String password){}

	@GetMapping("/validate")
	public ResponseEntity<String> validate(@RequestHeader("Authorization") String header){
		try{
			String token = header.substring(7);
			String email = jwtService.extractEmail(token);
			if(email == null){
				return ResponseEntity.status(401).body("Token Invalido");
			}
			return ResponseEntity.ok("Token valido");
		} catch (Exception e){
			return ResponseEntity.status(401).body("Token Invalido");
		}
	}

}
