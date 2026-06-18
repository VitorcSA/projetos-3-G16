package com.sintropia.calculator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.response.SummaryResponseDTO;
import com.sintropia.calculator.service.SummaryService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final UserService userService;
    private final SummaryService summaryService;

    public SummaryController(UserService userService, SummaryService summaryService) {
        this.userService = userService;
        this.summaryService = summaryService;
    }

    @GetMapping
    public ResponseEntity<?> getSummary(@AuthenticationPrincipal String email) {
        try {
            UserDTO user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("Usuário não encontrado");

            SummaryResponseDTO summary = summaryService.getSummary(user);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao carregar resumo: " + e.getMessage());
        }
    }
}