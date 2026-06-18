package com.sintropia.calculator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sintropia.calculator.model.MonthlyRecord;

public interface MonthlyRecordRepository extends JpaRepository<MonthlyRecord, Long>{

	@Query("SELECT r FROM MonthlyRecord r WHERE r.user.id = :userId AND MONTH(r.recordDate) = :month AND YEAR(r.recordDate) = :year")
    Optional<MonthlyRecord> findByUserAndMonthAndYear(
            @Param("userId") Long userId, 
            @Param("month") int month, 
            @Param("year") int year
    );
	
}
