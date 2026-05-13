package com.sintropia.calculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sintropia.calculator.model.Calculo;

public interface CalculoRepository extends JpaRepository<Calculo, Long> {
    Calculo findTopByOrderByIdDesc();
}