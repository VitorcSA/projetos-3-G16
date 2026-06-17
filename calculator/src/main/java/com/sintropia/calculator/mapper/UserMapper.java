package com.sintropia.calculator.mapper;

import org.springframework.stereotype.Component;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.model.User;

@Component
public class UserMapper implements Mapper<User, UserDTO>{
	private final AddressMapper addressMapper;
	
	public UserMapper(AddressMapper addressMapper) {
		this.addressMapper = addressMapper;
	}

	public UserDTO toDTO(User user) {
		return user == null ? null : new UserDTO(user.getName(),user.getEmail(),user.getStaffCount(),addressMapper.toDTO(user.getAddress()),user.getDigitalStaffCount());
	}
	
}
