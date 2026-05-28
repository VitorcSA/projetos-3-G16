package com.sintropia.calculator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User{
	
	@Id@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column
	private int staffCount;

	@Embedded
	@Column(nullable = false)
	private Address address;
	
	@Column
	private Double digitalPercentage;

	public User(){}

	public User(String name,String email,String password,int staffCount,Address address){
		this.name = name;
		this.email = email;
		this.password = password;
		this.staffCount = staffCount;
		this.address = address;
	}
	
	public User(String name,String email,String password,int staffCount,Address address,Double 
			porcentOfDigital){
		this(name,email,password,staffCount,address);
		this.digitalPercentage = porcentOfDigital;
	}

	public Long getId(){
		return this.id;
	}
	public void setId(Long id){
		this.id = id;
	}

	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}

	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email = email;
	}

	public String getPassword(){
		return this.password;
	}
	public void setPassword(String password){
		this.password = password;
	}

	public int getStaffCount(){
		return this.staffCount;
	}
	public void setStaffCount(int staffCount){
		this.staffCount = staffCount;
	}

	public Address getAddress(){
		return this.address;
	}
	public void setAddress(Address address){
		this.address = address;
	}
	public void setDigitalPercentage(Double porcentOfDigital){
		this.digitalPercentage = porcentOfDigital;
	}
	public Double getDigitalPercentage(){
		return this.digitalPercentage;
	}
	
}
