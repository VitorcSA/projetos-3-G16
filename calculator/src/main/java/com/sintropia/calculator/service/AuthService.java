package com.sintropia.calculator.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.request.LoginDTO;
import com.sintropia.calculator.dto.request.RegisterRequestDTO;
import com.sintropia.calculator.exception.BusinessException;
import com.sintropia.calculator.exception.InvalidCredentialsException;
import com.sintropia.calculator.model.Address;
import com.sintropia.calculator.model.User;

@Service
public class AuthService {
	
	private final JwtService jwtService;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	
	public AuthService(JwtService jwtService,UserService userService,PasswordEncoder passwordEncoder) {
		this.jwtService = jwtService;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}
	
	public String login(LoginDTO loginDTO) throws InvalidCredentialsException {
		User user = userService.findByEmail(loginDTO.email());
		
		if(user == null) throw new InvalidCredentialsException();
	
		if(!passwordEncoder.matches(loginDTO.password(),user.getPassword())) throw new InvalidCredentialsException();
	
		return jwtService.generateToken(loginDTO.email());
	}
	
	public String register(RegisterRequestDTO registerDTO) throws BusinessException{
		if (registerDTO.digitalCardStaffCount() != null) {
			if (registerDTO.digitalCardStaffCount() < 0) {
				throw new BusinessException("O número de usuários do cartão não pode ser negativo");
			}
			if (registerDTO.digitalCardStaffCount() > registerDTO.staffCount()) {
				throw new BusinessException("O número de usuários do cartão não pode ser maior que o total de funcionários");
			}
		}
		
		Address address = new Address(
				registerDTO.address().city(),
				registerDTO.address().state()
			);
		
		User user = new User(registerDTO.name(), registerDTO.email(), registerDTO.password(), registerDTO.staffCount(), address);
		
		if (registerDTO.digitalCardStaffCount() != null && registerDTO.staffCount() != null && registerDTO.staffCount() > 0) {
			double percentage = ((double) registerDTO.digitalCardStaffCount() / registerDTO.staffCount()) * 100.0;
			user.setDigitalPercentage(percentage);
		}

		userService.register(user);
		
		return jwtService.generateToken(registerDTO.email());
	}
	
	public boolean validate(String token) {
	    if (token == null) return false;

	    String email = jwtService.extractEmail(token);

	    return email != null && userService.findByEmail(email) != null;
	}
}
