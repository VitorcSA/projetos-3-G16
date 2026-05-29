package com.sintropia.calculator.dto.request;

import com.sintropia.calculator.dto.AddressDTO;

public record RegisterRequestDTO(String name,String email,String password,Integer staffCount,AddressDTO address,Long digitalCardStaffCount) {

}
