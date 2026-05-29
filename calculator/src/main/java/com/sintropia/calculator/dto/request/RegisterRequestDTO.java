package com.sintropia.calculator.dto.request;

import com.sintropia.calculator.dto.AddressDTO;

public record RegisterRequestDTO(String name,String email,String password,int staffCount,AddressDTO address) {

}
