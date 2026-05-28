package com.sintropia.calculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.sintropia.calculator.repository.UserRepository;
import com.sintropia.calculator.model.User;

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

}
