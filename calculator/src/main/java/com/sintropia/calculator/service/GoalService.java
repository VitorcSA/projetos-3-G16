package com.sintropia.calculator.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sintropia.calculator.dto.UserDTO;
import com.sintropia.calculator.dto.request.GoalRequestDTO;
import com.sintropia.calculator.dto.response.GoalResponseDTO;
import com.sintropia.calculator.model.Goal;
import com.sintropia.calculator.model.GoalType;
import com.sintropia.calculator.model.User;
import com.sintropia.calculator.repository.GoalRepository;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final SummaryService summaryService;

    public GoalService(GoalRepository goalRepository, SummaryService summaryService) {
        this.goalRepository = goalRepository;
        this.summaryService = summaryService;
    }

    public List<GoalResponseDTO> listGoals(User user) throws Exception {
        List<Goal> goals = goalRepository.findByUserIdOrderByTargetDateAsc(user.getId());
        List<GoalResponseDTO> result = new java.util.ArrayList<>();

        UserDTO userDTO = toUserDTO(user);

        for (Goal goal : goals) {
            double currentValue = summaryService.getCurrentValueForGoalType(userDTO, goal.getType());
            result.add(toDTO(goal, currentValue));
        }

        return result;
    }

    public GoalResponseDTO createGoal(User user, GoalRequestDTO data) throws Exception {
        if (data.type() == null) throw new IllegalArgumentException("Tipo de meta é obrigatório");
        if (data.targetDate() == null || data.targetDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data alvo deve ser no futuro");
        }

        Goal goal = new Goal(data.type(), data.targetValue(), data.targetDate(), user);
        goal = goalRepository.save(goal);

        UserDTO userDTO = toUserDTO(user);
        double currentValue = summaryService.getCurrentValueForGoalType(userDTO, goal.getType());
        return toDTO(goal, currentValue);
    }

    public GoalResponseDTO updateGoal(User user, Long goalId, GoalRequestDTO data) throws Exception {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new IllegalArgumentException("Meta não encontrada"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Meta não encontrada");
        }

        if (data.type() != null) goal.setType(data.type());
        if (data.targetDate() != null) goal.setTargetDate(data.targetDate());
        goal.setTargetValue(data.targetValue());

        goal = goalRepository.save(goal);

        UserDTO userDTO = toUserDTO(user);
        double currentValue = summaryService.getCurrentValueForGoalType(userDTO, goal.getType());
        return toDTO(goal, currentValue);
    }

    public void deleteGoal(User user, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new IllegalArgumentException("Meta não encontrada"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Meta não encontrada");
        }

        goalRepository.delete(goal);
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(
            user.getName(),
            user.getEmail(),
            user.getStaffCount(),
            null,
            user.getDigitalStaffCount(),
            null,
            user.getIndustrySector()
        );
    }

    private GoalResponseDTO toDTO(Goal goal, double currentValue) {
        GoalType type = goal.getType();
        return new GoalResponseDTO(
            goal.getId(),
            type.name(),
            type.getLabel(),
            type.getUnit(),
            goal.getTargetValue(),
            currentValue,
            goal.getTargetDate()
        );
    }
}