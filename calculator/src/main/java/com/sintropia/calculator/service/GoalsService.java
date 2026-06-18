package com.sintropia.calculator.service;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.DashboardGoalsDTO;
import com.sintropia.calculator.dto.UserDTO;

@Service
public class GoalsService {
	
	public DashboardGoalsDTO calculateGoals(UserDTO user) {
		if(user.digitalCardStaffCount() == null) return null;
		
	    double current = (user.digitalCardStaffCount() * 100.0 / user.staffCount());
	    long remaining = user.staffCount() - user.digitalCardStaffCount();
	    
	    return new DashboardGoalsDTO(
	        current,
	        remaining,
	        "Você está a " + remaining + " colaboradores da meta total!"
	    );
	}
}
