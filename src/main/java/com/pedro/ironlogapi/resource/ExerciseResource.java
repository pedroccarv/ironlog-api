package com.pedro.ironlogapi.resource;

import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/exercise")
public class ExerciseResource {

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<Exercise>> getAll() {
        List<Exercise> exercises = exerciseService.findAll();
        return ResponseEntity.ok().body(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getById(@PathVariable Long id) {
        Exercise exercise = exerciseService.findById(id);
        return ResponseEntity.ok().body(exercise);
    }

}
