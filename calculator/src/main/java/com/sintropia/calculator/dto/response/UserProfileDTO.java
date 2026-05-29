package com.sintropia.calculator.dto.response;

import com.sintropia.calculator.dto.AddressDTO;

public record UserProfileDTO(String name,String email,int staffCount,AddressDTO address) {

}
