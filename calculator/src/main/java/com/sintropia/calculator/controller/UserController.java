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
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("api/user")
public class UserController{

	private final UserService service;

	public UserController(UserService service){
		this.service = service;
	}

	@GetMapping("/profile")
	public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal String email){
		User user = service.findByEmail(email);
		
		if(user == null) {
			return ResponseEntity.status(404).body("Usuario não encontrado");
		}
	
		AddressDTO addressDTO = null;
		if(user.getAddress() != null) {
			addressDTO = new AddressDTO(
					user.getAddress().getStreet(),
					user.getAddress().getNumber(),
					user.getAddress().getCity(),
					user.getAddress().getState(),
					user.getAddress().getZipCode()
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
	public ResponseEntity<String> updateUserProfile(@AuthenticationPrincipal String email, @RequestBody RegisterRequestDTO updatedProfile) {
		try {
			service.updateProfile(email, updatedProfile);
			return ResponseEntity.ok("Perfil atualizado com sucesso!");
			
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao atualizar o perfil: " + e.getMessage());
		}
	}
	
}
