package com.sintropia.calculator.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address{
	private String city;
	private String state;

	public Address(){}

	public Address(String city,String state){
		this.city = city;
		this.state = state;
	}
	
	public String getCity(){
		return this.city;
	}
	public void setCity(String city){
		this.city = city;
	}

	public String getState(){
		return this.state;
	}
	public void setState(String state){
		this.state = state;
	}

}
