package com.sintropia.calculator.service;

public class ClimatiqRequest{
	public EmissionFactor emission_factor;
	public Parameters parameters;

	public ClimatiqRequest(String activityId,double weight,String unit){
		this.emission_factor = new EmissionFactor(activityId, "33.33");
		this.parameters = new Parameters(weight,unit);
	}

	record EmissionFactor(String activity_id, String data_version) {
	}
	record Parameters(double weight,String weight_unit) {
	}
}
