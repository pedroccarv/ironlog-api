package com.pedro.ironlogapi.resource;

import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workouts")
public class WorkoutResource {

    @Autowired
    private WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        List<Workout> workouts = workoutService.getAllWorkouts();
        return ResponseEntity.ok().body(workouts);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable Long id) {
        Workout workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok().body(workout);
    }

}
