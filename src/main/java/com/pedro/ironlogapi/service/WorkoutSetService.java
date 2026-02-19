package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.WorkoutSet;
import com.pedro.ironlogapi.repositories.WorkoutSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutSetService {

    @Autowired
    private WorkoutSetRepository workoutSetRepository;

    public List<WorkoutSet> getAllWorkoutSets() {
        return workoutSetRepository.findAll();
    }

    public WorkoutSet getWorkoutSetById(Long id) {
        Optional<WorkoutSet> workoutSet = workoutSetRepository.findById(id);
        return workoutSet.orElseThrow(() -> new RuntimeException("Workout Set not found"));
    }
}
