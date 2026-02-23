    package com.pedro.ironlogapi.DTO;


    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Getter
    @Setter
    @NoArgsConstructor
    public class WorkoutSetRequestDTO {

        private Integer sets;
        private Integer reps;
        private Double weight;
        private Long exerciseId;
        private Long workoutId;

    }
