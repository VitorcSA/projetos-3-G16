package com.sintropia.calculator.service;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.request.RegisterRequestDTO;
import com.sintropia.calculator.model.Address;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.repository.UserRepository;
import org.springframework.util.StringUtils;

@Service
public class UserService{

	@Autowired
	private UserRepository repository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	public User register(User user){

		if(repository.existsByEmail(user.getEmail())){
			throw new IllegalArgumentException("Este e-mail já existe");
		}

		if(repository.existsByName(user.getName())){
			throw new IllegalArgumentException("Este nome já existe");
		}
		
		String password = encoder.encode(user.getPassword());
		user.setPassword(password);
	
		return repository.save(user);
	}
	
	public User updateProfile(String currentEmail, RegisterRequestDTO data) {
		User user = repository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
		
		if (StringUtils.hasText(data.email()) && !user.getEmail().equals(data.email())) {
			if (repository.existsByEmail(data.email())) {
				throw new IllegalArgumentException("Email invalido.");
			}
			user.setEmail(data.email());
		}
		
		updateIfValid(data.name(), user::setName);
		updateIfValid(data.password(), pwd -> user.setPassword(encoder.encode(pwd)));
		
		if (data.staffCount() != null) user.setStaffCount(data.staffCount());
		if (data.digitalCardStaffCount() != null && user.getStaffCount() > 0) {
		    user.setDigitalPercentage(((double) data.digitalCardStaffCount() / user.getStaffCount()) * 100);
		}
		
		if (data.address() != null) {
			if (user.getAddress() == null) user.setAddress(new Address());
			
			Address address = user.getAddress();
			
			updateIfValid(data.address().city(), address::setCity);
			updateIfValid(data.address().state(), address::setState);
		}
		
		return repository.save(user);
	}
	
	private void updateIfValid(String value,Consumer<String> setter) {
		if(StringUtils.hasText(value)) {
			setter.accept(value);
		}
	}

	public User findByEmail(String email){
		return repository.findByEmail(email).orElse(null);
	}

	public User findByName(String name) {
		return repository.findByName(name).orElse(null);
	}
	
}
