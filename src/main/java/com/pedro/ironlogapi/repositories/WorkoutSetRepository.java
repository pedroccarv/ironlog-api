package com.pedro.ironlogapi.repositories;

import com.pedro.ironlogapi.entities.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query("SELECT MAX(ws.weight) FROM WorkoutSet ws Where ws.exercise.id = :exerciseId AND ws.workout.user.id = :userId")
    Double findMaxWeight(@Param("exerciseId") Long exerciseId, @Param("userId") Long userId);

}
