package com.pedro.ironlogapi.resource;

import com.pedro.ironlogapi.entities.WorkoutSet;
import com.pedro.ironlogapi.service.WorkoutSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workout-sets")
public class WorkoutSetResource {

    @Autowired
    private WorkoutSetService workoutSetService;

    @GetMapping
    public ResponseEntity<List<WorkoutSet>> getWorkoutSets() {
        List<WorkoutSet> workoutSets = workoutSetService.getAllWorkoutSets();
        return ResponseEntity.ok().body(workoutSets);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<WorkoutSet> getWorkoutSetById(@PathVariable Long id) {
        WorkoutSet workoutSet = workoutSetService.getWorkoutSetById(id);
        return ResponseEntity.ok().body(workoutSet);
    }

}
