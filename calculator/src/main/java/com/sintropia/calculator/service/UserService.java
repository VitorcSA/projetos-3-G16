package com.sintropia.calculator.service;

import java.util.function.Consumer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.request.RegisterRequestDTO;
import com.sintropia.calculator.mapper.UserMapper;
import com.sintropia.calculator.model.Address;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.repository.UserRepository;

@Service
public class UserService{

	private final UserRepository repository;
	
	private final UserMapper mapper;

	private final BCryptPasswordEncoder encoder;

	public UserService(UserRepository repository,UserMapper mapper,BCryptPasswordEncoder encoder) {
		this.repository = repository;
		this.mapper = mapper;
		this.encoder = encoder;
	}
	
	public UserDTO register(User user){

		if(repository.existsByEmail(user.getEmail())){
			throw new IllegalArgumentException("Este e-mail já existe");
		}

		if(repository.existsByName(user.getName())){
			throw new IllegalArgumentException("Este nome já existe");
		}
		
		String password = encoder.encode(user.getPassword());
		user.setPassword(password);
	
		return mapper.toDTO(repository.save(user));
	}
	
	public UserDTO updateProfile(String currentEmail, RegisterRequestDTO data) {
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
		
		if (data.digitalCardStaffCount() != null && user.getStaffCount() > 0) user.setDigitalStaffCount(data.digitalCardStaffCount());
		
		if (data.industrySector() != null) user.setIndustrySector(data.industrySector());
		
		if (data.address() != null) {
			if (user.getAddress() == null) user.setAddress(new Address());
			
			Address address = user.getAddress();
			
			updateIfValid(data.address().city(), address::setCity);
			updateIfValid(data.address().state(), address::setState);
		}
		
		return mapper.toDTO(repository.save(user));
	}
	
	private void updateIfValid(String value,Consumer<String> setter) {
		if(StringUtils.hasText(value)) {
			setter.accept(value);
		}
	}

	public UserDTO findByEmail(String email){
		return mapper.toDTO(getUserEntityByEmail(email));
	}

	public UserDTO findByName(String name) {
		return mapper.toDTO(getUserEntityByName(name));
	}
	
	public User getUserEntityByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }
	
	public User getUserEntityByName(String name) {
        return repository.findByName(name).orElse(null);
    }
	
}
