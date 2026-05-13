package com.sintropia.calculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sintropia.calculator.dto.CalculoRequestDTO;
import com.sintropia.calculator.dto.CalculoResponseDTO;
import com.sintropia.calculator.service.CalculadoraService;

@RestController
@RequestMapping("/api/calculator")
@CrossOrigin(origins = "*")
public class CalculatorController {

    private final CalculadoraService service;

    public CalculatorController(CalculadoraService service) {
        this.service = service;
    }

    @PostMapping("/calcular")
    public ResponseEntity<CalculoResponseDTO> calcular() {

        CalculoRequestDTO request = service.buscarDadosDoBanco();

        CalculoResponseDTO response = service.calcular(request);

        return ResponseEntity.ok(response);
    }
}