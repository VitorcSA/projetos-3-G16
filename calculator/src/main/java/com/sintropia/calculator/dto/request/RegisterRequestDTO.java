package com.sintropia.calculator.dto.request;

public record RegisterRequestDTO(String name,String email,String password,int staffCount,AddressDTO address) {

}
