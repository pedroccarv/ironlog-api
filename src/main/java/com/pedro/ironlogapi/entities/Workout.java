package com.pedro.ironlogapi.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Data
@Table(name = "tb_workout")
@Entity
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private Instant date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "workout")
    private List<WorkoutSet> sets = new ArrayList<>();

    public Workout(Long id, String title, Instant date, User user) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.user = user;
    }

}
