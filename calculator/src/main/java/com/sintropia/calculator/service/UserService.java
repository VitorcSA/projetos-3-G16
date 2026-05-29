package com.sintropia.calculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.request.RegisterRequestDTO;
import com.sintropia.calculator.model.Address;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.repository.UserRepository;

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
	
    public User updateDigitalCardPercentage(Long userId, Double percentage) {
        User user = repository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        user.setDigitalPercentage(percentage);
        return repository.save(user);
    }

	public User findByEmail(String email){
		return repository.findByEmail(email).orElse(null);
	}

	public User findByName(String name) {
		return repository.findByName(name).orElse(null);
	}
	
	public User updateProfile(String currentEmail, RegisterRequestDTO data) {
		User user = repository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
		
		if (!user.getEmail().equals(data.email()) && repository.existsByEmail(data.email())) {
			throw new IllegalArgumentException("Email invalido.");
		}
		
		user.setName(data.name());
		user.setEmail(data.email());
		user.setStaffCount(data.staffCount());
		
		if (data.password() != null && !data.password().trim().isEmpty()) {
			String newPasswordEncrypted = encoder.encode(data.password());
			user.setPassword(newPasswordEncrypted);
		}
		
		if (data.address() != null) {
			if (user.getAddress() == null) {
				user.setAddress(new Address());
			}
			user.getAddress().setStreet(data.address().street());
			user.getAddress().setNumber(data.address().number());
			user.getAddress().setCity(data.address().city());
			user.getAddress().setState(data.address().state());
			user.getAddress().setZipCode(data.address().zipCode());
		}
		
		return repository.save(user);
	}

}
