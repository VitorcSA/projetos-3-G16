package com.sintropia.calculator.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.MonthlyRecordService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("/api/records")
public class RecordRestController {

    private final MonthlyRecordService monthlyRecordService;
    private final UserService userService;

    public RecordRestController(MonthlyRecordService monthlyRecordService, UserService userService) {
        this.monthlyRecordService = monthlyRecordService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOrUpdateRecord(@AuthenticationPrincipal String email) { 
        
        if (email == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        User user = userService.getUserEntityByEmail(email);
        if (user ==  null) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }

        try {
            monthlyRecordService.AddOrEditRegistry(user);
            
            return ResponseEntity.ok().body(Map.of("message", "Registro salvo com sucesso!"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao salvar registro: " + e.getMessage());
        }
    }
}