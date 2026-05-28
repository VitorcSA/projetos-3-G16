package com.sintropia.calculator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sintropia.calculator.model.User;

public interface UserRepository extends JpaRepository<User,Long>{
	boolean existsByEmail(String email);
	boolean existsByName(String name);

	Optional<User> findByEmail(String email);
	Optional<User> findByName(String name);
}