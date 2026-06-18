package com.sintropia.calculator.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "monthly_records")
public class MonthlyRecord {
	
	@Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "staff_count", nullable = false)
	private Long staffCount;
	
	@Column(name = "digital_staff_count", nullable = false)
	private Long digitalStaffCount;
	
	private LocalDate recordDate;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public MonthlyRecord() {}
	
	public MonthlyRecord(Long staffCount,Long digitalStaffCount,LocalDate recordDate) {
		this.staffCount = staffCount;
		this.digitalStaffCount = digitalStaffCount;
		this.recordDate = recordDate;
	}
	
	public MonthlyRecord(Long staffCount,Long digitalStaffCount,LocalDate recordDate,User user) {
		this(staffCount,digitalStaffCount,recordDate);
		this.user = user;
	}

	public Long getId() {
		return id;
	}
	
	public Long getStaffCount() {
		return staffCount;
	}
	
	public void setStaffCount(Long staffCount) {
		this.staffCount = staffCount;
	}
	
	public Long getDigitalStaffCount() {
		return digitalStaffCount;
	}
	
	public void setDigitalStaffCount(Long digitalStaffCount) {
		this.digitalStaffCount = digitalStaffCount;
	}
	
	public LocalDate getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(LocalDate recordDate) {
		this.recordDate = recordDate;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
}
