package com.sintropia.calculator.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sintropia.calculator.dto.request.GoalRequestDTO;
import com.sintropia.calculator.dto.response.GoalResponseDTO;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.service.GoalService;
import com.sintropia.calculator.service.UserService;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> listGoals(@AuthenticationPrincipal String email) {
        try {
            User user = userService.getUserEntityByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("Usuário não encontrado");
            List<GoalResponseDTO> goals = goalService.listGoals(user);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao listar metas: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createGoal(@AuthenticationPrincipal String email,
                                        @RequestBody GoalRequestDTO data) {
        try {
            User user = userService.getUserEntityByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("Usuário não encontrado");
            GoalResponseDTO goal = goalService.createGoal(user, data);
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar meta: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@AuthenticationPrincipal String email,
                                        @PathVariable Long id,
                                        @RequestBody GoalRequestDTO data) {
        try {
            User user = userService.getUserEntityByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("Usuário não encontrado");
            GoalResponseDTO goal = goalService.updateGoal(user, id, data);
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao atualizar meta: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@AuthenticationPrincipal String email,
                                        @PathVariable Long id) {
        try {
            User user = userService.getUserEntityByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("Usuário não encontrado");
            goalService.deleteGoal(user, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao excluir meta: " + e.getMessage());
        }
    }
}