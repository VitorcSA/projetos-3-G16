package com.sintropia.calculator.mapper;

import org.springframework.stereotype.Component;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.model.User;

@Component
public class UserMapper implements Mapper<User, UserDTO>{
	private final AddressMapper addressMapper;
	private final MonthlyRecordMapper recordMapper;
	
	public UserMapper(AddressMapper addressMapper,MonthlyRecordMapper recordMapper) {
		this.addressMapper = addressMapper;
		this.recordMapper = recordMapper;
	}

	public UserDTO toDTO(User user) {
	    return user == null ? null : 
	        new UserDTO(
	                user.getName(),
	                user.getEmail(),
	                user.getStaffCount(),
	                addressMapper.toDTO(user.getAddress()),
	                user.getDigitalStaffCount(),
	                recordMapper.toDTO(user.getMonthlyRecords()),
	                user.getIndustrySector());
	}
}
