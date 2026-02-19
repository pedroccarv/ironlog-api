package com.pedro.ironlogapi.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tb_workout_sets")
public class WorkoutSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer sets;
    private Integer reps;
    private Double weight;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

}
