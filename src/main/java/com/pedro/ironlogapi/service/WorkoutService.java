package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.repositories.WorkoutRepository;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        return workout.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Workout createWorkout(Workout workout) {
        return workoutRepository.save(workout);
    }

    public void delete(Long id){
        if (!workoutRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            workoutRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation: cannot delete Workout with existing workout sets.");
        }
    }

    public void updateData(Workout entity, Workout obj){
        entity.setTitle(obj.getTitle());
        entity.setDate(obj.getDate());
    }

    public Workout updateWorkout(Long id, Workout workout) {
        try {
            Workout workoutEntity = workoutRepository.getReferenceById(id);
            updateData(workoutEntity, workout);
            return workoutRepository.save(workoutEntity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    public List<Workout> findByUserId(Long userId) {
        return workoutRepository.findByUserId(userId);
    }
}