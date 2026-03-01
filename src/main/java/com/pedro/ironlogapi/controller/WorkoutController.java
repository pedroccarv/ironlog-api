package com.pedro.ironlogapi.controller;

import com.pedro.ironlogapi.DTO.WorkoutDTO;
import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<List<WorkoutDTO>> getAllWorkouts() {
        List<Workout> workouts = workoutService.getAllWorkouts();
        List<WorkoutDTO> listDto = workouts.stream().map(WorkoutDTO::new).toList();
        return ResponseEntity.ok().body(listDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<WorkoutDTO> getWorkoutById(@PathVariable Long id) {
        Workout workout = workoutService.getWorkoutById(id);
        WorkoutDTO workoutDto = new WorkoutDTO(workout);
        return ResponseEntity.ok().body(workoutDto);
    }

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout) {
        workout = workoutService.createWorkout(workout);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(workout.getId()).toUri();
        return ResponseEntity.created(uri).body(workout);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Workout> updateWorkout(@PathVariable Long id, @RequestBody Workout workout) {
        workout = workoutService.updateWorkout(id, workout);
        return ResponseEntity.ok().body(workout);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Page<WorkoutDTO>> findByUserId(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction) {

        Page<Workout> list = workoutService.findByUserId(userId, page, linesPerPage, orderBy, direction);
        Page<WorkoutDTO> listDto = list.map(WorkoutDTO::new);
        return ResponseEntity.ok().body(listDto);
    }

}
