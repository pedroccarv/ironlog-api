package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.WorkoutSet;
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

    public List<WorkoutSet> getAllWorkoutSets() {
        return workoutSetRepository.findAll();
    }

    public WorkoutSet getWorkoutSetById(Long id) {
        Optional<WorkoutSet> workoutSet = workoutSetRepository.findById(id);
        return workoutSet.orElseThrow(() -> new ResourceNotFoundException("Workout Set not found"));
    }

    public WorkoutSet createWorkoutSet(WorkoutSet workoutSet) {
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

    public void updateData (WorkoutSet entity, WorkoutSet obj) {
        entity.setWorkout(obj.getWorkout());
        entity.setSets(obj.getSets());
        entity.setWeight(obj.getWeight());
        entity.setReps(obj.getReps());
        entity.setExercise(obj.getExercise());
    }

    public WorkoutSet update (Long id, WorkoutSet obj) {
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
