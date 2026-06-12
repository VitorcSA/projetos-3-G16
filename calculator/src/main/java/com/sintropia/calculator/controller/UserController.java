package com.sintropia.calculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.dto.AddressDTO;
import com.sintropia.calculator.dto.request.RegisterRequestDTO;
import com.sintropia.calculator.dto.response.UserProfileDTO;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.JwtService;
import com.sintropia.calculator.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/user")
public class UserController extends AbstractController{

	private final UserService userService;
	private final JwtService jwtService;

	public UserController(UserService userService,JwtService jwtService){
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@GetMapping("/profile")
	public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal String email){
		User user = userService.findByEmail(email);
		
		if(user == null) {
			return ResponseEntity.status(404).body("Usuario não encontrado");
		}
	
		AddressDTO addressDTO = null;
		if(user.getAddress() != null) {
			addressDTO = new AddressDTO(
					user.getAddress().getCity(),
					user.getAddress().getState()
			);
		}
		
		Long digitalCardStaffCount = (user.getDigitalPercentage() != null) ? Math.round((user.getDigitalPercentage() * user.getStaffCount()) / 100.0) : null;
		
		UserProfileDTO profile = new UserProfileDTO(
		        user.getName(),
		        user.getEmail(),
		        user.getStaffCount(),
		        addressDTO,
		        digitalCardStaffCount
				);
		
		return ResponseEntity.ok(profile);
	}
	
	@PutMapping("/profile")
	public ResponseEntity<String> updateUserProfile(
			@AuthenticationPrincipal String email,
			@RequestBody RegisterRequestDTO updatedProfile,
			HttpServletResponse response){
		try {
			User user = userService.updateProfile(email, updatedProfile);

			if (!user.getEmail().equals(email)) {
			    String newToken = jwtService.generateToken(user.getEmail());
			    addCookie(response, newToken);
			}
			
			return ResponseEntity.ok("Perfil atualizado com sucesso!");
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao atualizar o perfil: " + e.getMessage());
		}
	}
	
}
