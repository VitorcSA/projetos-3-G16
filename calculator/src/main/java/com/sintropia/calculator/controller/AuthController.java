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
import com.sintropia.calculator.service.JwtService;
import com.sintropia.calculator.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

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
	public ResponseEntity<String> login(@RequestBody LoginDTO request, HttpServletResponse response){
		User user = userService.findByEmail(request.email());

		if(user == null){
			return ResponseEntity.status(401).body("Email ou senha invalidos");
		}

		if(!passwordEncoder.matches(request.password(),user.getPassword())){
			return ResponseEntity.status(401).body("Email ou senha invalidos");
		}

		String token = jwtService.generateToken(user.getEmail());
		addCookie(response, token);
		
		return ResponseEntity.ok("Login efetuado com sucesso");
	}
	
	@PostMapping("/register") 
	public ResponseEntity<String> registerController(@RequestBody RegisterRequestDTO request, HttpServletResponse response){
		try{
			if (request.digitalCardStaffCount() != null) {
				if (request.digitalCardStaffCount() < 0) {
					return ResponseEntity.badRequest().body("O número de usuários do cartão não pode ser negativo");
				}
				if (request.staffCount() != null && request.digitalCardStaffCount() > request.staffCount()) {
					return ResponseEntity.badRequest().body("O número de usuários do cartão não pode ser maior que o total de funcionários");
				}
			}

			Address address = new Address(
				request.address().city(),
				request.address().state()
			);

			User user = new User(request.name(), request.email(), request.password(), request.staffCount(), address);

			if (request.digitalCardStaffCount() != null && request.staffCount() != null && request.staffCount() > 0) {
				double percentage = ((double) request.digitalCardStaffCount() / request.staffCount()) * 100.0;
				user.setDigitalPercentage(percentage);
			}

			userService.register(user);
			
			String token = jwtService.generateToken(user.getEmail());
			addCookie(response, token);

			return ResponseEntity.ok("Usuário registrado e logado com sucesso");
			
		} catch (IllegalArgumentException error){
			return ResponseEntity.badRequest().body(error.getMessage());
		}
	}

	@GetMapping("/validate")
	public ResponseEntity<String> validate(@CookieValue(name = "AUTH_TOKEN", required = false) String token){
		if(token == null) {
			return ResponseEntity.status(401).body("Token não encontrado ou expirado");
		}
		
		try{
			String email = jwtService.extractEmail(token);
			if(email == null || userService.findByEmail(email) == null){
				return ResponseEntity.status(401).body("Token Invalido");
			}
			return ResponseEntity.ok("Token valido");
		} catch (Exception e){
			return ResponseEntity.status(401).body("Token Invalido");
		}
	}

	private void addCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
	
}
