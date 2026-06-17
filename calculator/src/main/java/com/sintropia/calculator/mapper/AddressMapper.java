package com.sintropia.calculator.mapper;

import org.springframework.stereotype.Component;

import com.sintropia.calculator.dto.AddressDTO;
import com.sintropia.calculator.model.Address;

@Component
public class AddressMapper implements Mapper<Address, AddressDTO> {
	public AddressDTO toDTO(Address address) {
		return address == null ? null : new AddressDTO(address.getCity(),address.getState());
	}
}
