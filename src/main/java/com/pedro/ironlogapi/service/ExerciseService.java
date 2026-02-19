package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.repositories.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    public Exercise findById(Long id) {
        Optional<Exercise> exercise = exerciseRepository.findById(id);
        return exercise.orElseThrow(() -> new RuntimeException("Exercise not found"));
    }

}
