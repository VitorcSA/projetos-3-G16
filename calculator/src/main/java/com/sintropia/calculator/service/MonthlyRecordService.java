package com.sintropia.calculator.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.exception.InsufficientDataException;
import com.sintropia.calculator.model.MonthlyRecord;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.repository.MonthlyRecordRepository;

import jakarta.transaction.Transactional;

@Service
public class MonthlyRecordService {

	private final MonthlyRecordRepository monthlyRecordRepository;

	public MonthlyRecordService(MonthlyRecordRepository monthlyRecordRepository) {
		this.monthlyRecordRepository = monthlyRecordRepository;
	}
	
	@Transactional
	public MonthlyRecord AddOrEditRegistry(User user) throws InsufficientDataException{
		if(user.getDigitalStaffCount() == null) throw new InsufficientDataException("Dados pendentes");
		
        LocalDate date = LocalDate.now();
        int month = date.getMonthValue();
        int year = date.getYear();

        Optional<MonthlyRecord> registry = monthlyRecordRepository.findByUserAndMonthAndYear(user.getId(), month, year);

        MonthlyRecord record;

        if (registry.isPresent()) {
            record = registry.get();
            record.setStaffCount(user.getStaffCount());
            record.setDigitalStaffCount(user.getDigitalStaffCount());
            record.setRecordDate(date); 
        } else {
            record = new MonthlyRecord(user.getStaffCount(), user.getDigitalStaffCount(), date, user);
        }

        return monthlyRecordRepository.save(record);
    }
	
}
