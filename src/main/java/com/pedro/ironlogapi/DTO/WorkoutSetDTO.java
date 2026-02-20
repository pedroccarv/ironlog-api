package com.pedro.ironlogapi.DTO;

import com.pedro.ironlogapi.entities.WorkoutSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class WorkoutSetDTO implements Serializable {

    private Long id;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private String workoutTitle;
    private String exerciseName;

    public WorkoutSetDTO(WorkoutSet obj) {
        this.id = obj.getId();
        this.sets = obj.getSets();
        this.reps = obj.getReps();
        this.weight = obj.getWeight();
        this.workoutTitle = obj.getWorkout().getTitle();
        this.exerciseName = obj.getExercise().getName();
    }

}
