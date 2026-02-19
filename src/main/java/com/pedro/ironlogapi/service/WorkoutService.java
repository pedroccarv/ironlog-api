package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.repositories.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAll();
    }

    public Workout getWorkoutById(Long id) {
        Optional<Workout> workout = workoutRepository.findById(id);
        return workout.orElseThrow(() -> new RuntimeException("Workout not found"));
    }

}
