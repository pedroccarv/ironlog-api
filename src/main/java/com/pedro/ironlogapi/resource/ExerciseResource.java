package com.pedro.ironlogapi.resource;

import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<Exercise> create(@RequestBody Exercise obj) {
        obj = exerciseService.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Exercise> update(@PathVariable Long id, @RequestBody Exercise obj) {
        obj = exerciseService.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

}
