package com.pedro.ironlogapi.repositories;

import com.pedro.ironlogapi.entities.Workout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    Page<Workout> findByUserId(Long userId, Pageable pageable);

}
