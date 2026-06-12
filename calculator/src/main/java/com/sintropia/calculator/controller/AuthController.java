package com.sintropia.calculator.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.dto.request.LoginDTO;
import com.sintropia.calculator.dto.request.RegisterRequestDTO;
import com.sintropia.calculator.model.Address;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.AuthService;
import com.sintropia.calculator.service.JwtService;
import com.sintropia.calculator.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/auth")
public class AuthController extends AbstractController{

	private final AuthService authService;

	public AuthController(AuthService authService){
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginDTO request, HttpServletResponse response){
		try {
			addCookie(response,authService.login(request));
			return ResponseEntity.ok("Login efetuado com sucessor");
		}catch (Exception e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PostMapping("/register") 
	public ResponseEntity<String> registerController(@RequestBody RegisterRequestDTO request, HttpServletResponse response){
		try{
			addCookie(response,authService.register(request));
			return ResponseEntity.ok("Usuário registrado e logado com sucesso");
		} catch (IllegalArgumentException e){
			return ResponseEntity.status(400).body(e.getMessage());
		}
	}

	@GetMapping("/validate")
	public ResponseEntity<String> validate(@CookieValue(name = "AUTH_TOKEN", required = false) String token){
		if(authService.validate(token)) {
			return ResponseEntity.ok("Token valido");
		}
		return ResponseEntity.status(401).body("Token invalido");
	}
	
}
