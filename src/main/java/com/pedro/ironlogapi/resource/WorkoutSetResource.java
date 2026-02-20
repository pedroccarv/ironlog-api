package com.pedro.ironlogapi.resource;

import com.pedro.ironlogapi.DTO.WorkoutSetDTO;
import com.pedro.ironlogapi.entities.WorkoutSet;
import com.pedro.ironlogapi.service.WorkoutSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/workout-sets")
public class WorkoutSetResource {

    @Autowired
    private WorkoutSetService workoutSetService;

    @GetMapping
    public ResponseEntity<List<WorkoutSetDTO>> getWorkoutSets() {
        List<WorkoutSet> workoutSets = workoutSetService.getAllWorkoutSets();
        List<WorkoutSetDTO> listDto = workoutSets.stream().map(WorkoutSetDTO::new).toList();
        return ResponseEntity.ok().body(listDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<WorkoutSetDTO> getWorkoutSetById(@PathVariable Long id) {
        WorkoutSet workoutSet = workoutSetService.getWorkoutSetById(id);
        WorkoutSetDTO workoutSetDTO = new WorkoutSetDTO(workoutSet);
        return ResponseEntity.ok().body(workoutSetDTO);
    }

    @PostMapping
    public ResponseEntity<WorkoutSet> createWorkoutSet(@RequestBody WorkoutSet workoutSet) {
        workoutSet = workoutSetService.createWorkoutSet(workoutSet);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(workoutSet.getId()).toUri();
        return ResponseEntity.created(uri).body(workoutSet);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteWorkoutSet(@PathVariable Long id) {
        workoutSetService.deleteWorkoutSetById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<WorkoutSet> updateWorkoutSet(@RequestBody WorkoutSet obj, @PathVariable Long id) {
        obj = workoutSetService.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

}
