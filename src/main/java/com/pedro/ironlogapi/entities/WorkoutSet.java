package com.pedro.ironlogapi.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    @NotNull(message = "O número de séries não pode ser nulo")
    @Positive(message = "O número de séries deve ser maior que zero")
    private Integer sets;
    @NotNull(message = "O número de repetições não pode ser nulo")
    @Positive(message = "O número de repetições deve ser maior que zero")
    private Integer reps;
    @NotNull(message = "O peso não pode ser nulo")
    @PositiveOrZero(message = "O peso não pode ser negativo")
    private Double weight;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

}
