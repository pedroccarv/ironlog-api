package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.DTO.WorkoutSetRequestDTO;
import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.entities.WorkoutSet;
import com.pedro.ironlogapi.repositories.ExerciseRepository;
import com.pedro.ironlogapi.repositories.WorkoutRepository;
import com.pedro.ironlogapi.repositories.WorkoutSetRepository;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutSetService {

    @Autowired
    private WorkoutSetRepository workoutSetRepository;
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<WorkoutSet> getAllWorkoutSets() {
        return workoutSetRepository.findAll();
    }

    public WorkoutSet getWorkoutSetById(Long id) {
        Optional<WorkoutSet> workoutSet = workoutSetRepository.findById(id);
        return workoutSet.orElseThrow(() -> new ResourceNotFoundException("Workout Set not found"));
    }

    public WorkoutSet createWorkoutSet(WorkoutSetRequestDTO dto) {
        Workout workout = workoutRepository.findById(dto.getWorkoutId())
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found"));

        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

        WorkoutSet workoutSet = WorkoutSet.builder()
                .sets(dto.getSets())
                .reps(dto.getReps())
                .weight(dto.getWeight())
                .workout(workout)
                .exercise(exercise)
                .build();

        return workoutSetRepository.save(workoutSet);
    }

    public void deleteWorkoutSetById(Long id) {
        if (!workoutSetRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            workoutSetRepository.deleteById(id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Workout Set cannot be deleted");
        }
    }

    public void updateData (WorkoutSet entity, WorkoutSetRequestDTO obj) {
        Workout workout = workoutRepository.findById(obj.getWorkoutId()).orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
        Exercise exercise = exerciseRepository.findById(obj.getExerciseId()).orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

        entity.setSets(obj.getSets());
        entity.setReps(obj.getReps());
        entity.setWeight(obj.getWeight());
        entity.setWorkout(workout);
        entity.setExercise(exercise);
    }

    public WorkoutSet update (Long id, WorkoutSetRequestDTO obj) {
        try {
            WorkoutSet entity = workoutSetRepository.getReferenceById(id);
            updateData(entity, obj);
            return workoutSetRepository.save(entity);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException(id);
        }
    }

    public Double getPersonalRecord(Long exerciseId, Long userId){
        Double maxWeight = workoutSetRepository.findMaxWeight(exerciseId, userId);
        return maxWeight != null ? maxWeight : 0.0;
    }

}
