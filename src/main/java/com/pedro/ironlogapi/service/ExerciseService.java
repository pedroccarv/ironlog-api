package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.repositories.ExerciseRepository;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    public Exercise insert(Exercise obj) {
        return exerciseRepository.save(obj);
    }

    public void delete(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exercise not found");
        }
        try {
            exerciseRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation: cannot delete with existing workout sets");
        }
    }

    public void updateData(Exercise entity, Exercise obj){
        entity.setName(obj.getName());
        entity.setId(obj.getId());
    }

    public Exercise update(Long id, Exercise obj) {
        try {
            Exercise entity = exerciseRepository.getReferenceById(id);
            updateData(entity, obj);
            return exerciseRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

}
