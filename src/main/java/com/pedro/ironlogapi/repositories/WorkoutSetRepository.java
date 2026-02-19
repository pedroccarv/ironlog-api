package com.pedro.ironlogapi.repositories;

import com.pedro.ironlogapi.entities.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
}
