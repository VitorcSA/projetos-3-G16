package com.sintropia.calculator.strategy;

import com.sintropia.calculator.dto.UserDTO;

public interface DashboardExporter {
	
	    String getDashboardId(); 
	    byte[] exportExcel(UserDTO user) throws Exception;
	    byte[] exportPdf(UserDTO user) throws Exception;
	
}
