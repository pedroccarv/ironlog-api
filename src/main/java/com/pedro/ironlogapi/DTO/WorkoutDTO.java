package com.pedro.ironlogapi.DTO;

import com.pedro.ironlogapi.entities.Workout;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WorkoutDTO implements Serializable {

    private Long id;
    private String title;
    private Instant date;

    private List<WorkoutSetDTO> sets;

    public WorkoutDTO(Workout obj) {
        this.id = obj.getId();
        this.title = obj.getTitle();
        this.date = obj.getDate();
        this.sets = obj.getSets().stream().map(WorkoutSetDTO::new).toList();
    }

}
