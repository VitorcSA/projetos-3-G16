package com.sintropia.calculator.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sintropia.calculator.dto.MonthlyRecordDTO;
import com.sintropia.calculator.model.MonthlyRecord;

import io.jsonwebtoken.lang.Collections;

@Component
public class MonthlyRecordMapper implements Mapper<MonthlyRecord, MonthlyRecordDTO>{

	public MonthlyRecordDTO toDTO(MonthlyRecord record){
		return new MonthlyRecordDTO(record.getRecordDate(),record.getStaffCount(),record.getDigitalStaffCount());
	}
	
    public List<MonthlyRecordDTO> toDTO(List<MonthlyRecord> records) {
        if (records == null) return Collections.emptyList();

	        return records.stream()
                      .map(this::toDTO) 
                      .toList();
    }
	
}
