package com.sintropia.calculator.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address{
	private String street;
	private String number;
	private String city;
	private String state;
	private String zipCode;

	public Address(){}

	public Address(String street,String number,String city,String state,String zipCode){
		this.street = street;
		this.number = number;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
	}

	String getStreet(){
		return this.street;
	}
	void setStreet(String street){
		this.street = street;
	}

	String getNumber(){
		return this.number;
	}
	void setNumber(String number){
		this.number = number;
	}

	String getCity(){
		return this.city;
	}
	void setCity(String city){
		this.city = city;
	}

	String getState(){
		return this.state;
	}
	void setState(String state){
		this.state = state;
	}

	String getZipCode(){
		return this.zipCode;
	}
	void setZipCode(String zipCode){
		this.zipCode = zipCode;
	}
}
