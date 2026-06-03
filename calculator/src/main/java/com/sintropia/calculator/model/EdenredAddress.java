package com.sintropia.calculator.model;

import java.util.Arrays;
import java.util.Comparator;

public enum EdenredAddress {

	FACTORY_1("Colombo", "Paraná",25.3852812,-49.1649019),
	FACTORY_2("São Bernardo do Campo", "São Paulo",-23.6743634,-46.5915167),
	FACTORY_3("Cotia", "São Paulo",-23.6051294,-46.8734033);
	
	private final String city;
	private final String state;
	private final Coordinates coordinates;
	
	private EdenredAddress(String city,String state,double latitude,double longitude) {
		this.city = city;
		this.state = state;
		this.coordinates = new Coordinates(latitude,longitude);
	}
	
	public Coordinates getCoordinates() {return this.coordinates;}
	public String getCity() {return this.city;}
	public String getState() {return this.state;}
}
	
	