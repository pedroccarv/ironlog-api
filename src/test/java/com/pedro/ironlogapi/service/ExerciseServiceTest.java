package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.repositories.ExerciseRepository;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise;

    @BeforeEach
    void setUp() {
        this.exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("Supino");
        exercise.setMuscleGroup("Chest");
    }

    @Test
    @DisplayName("Deve retornar uma lista de exercicios")
    void findAll_ShouldReturnListOfExercises(){
        Exercise exercise2 = new Exercise();
        exercise2.setId(2L);
        exercise2.setName("Agachamento");
        exercise2.setMuscleGroup("Legs");

        when(exerciseRepository.findAll()).thenReturn(Arrays.asList(exercise, exercise2));

        List<Exercise> result = exerciseService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Supino", result.get(0).getName());
        assertEquals("Agachamento", result.get(1).getName());

        verify(exerciseRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando nao houver exercicios")
    void findAll_ShouldReturnEmptyList_WhenNoExerciseExist(){
        when(exerciseRepository.findAll()).thenReturn(List.of());

        List<Exercise> result = exerciseService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(exerciseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um exercicio quando o ID existir")
    void findById_ShouldReturnExercise_WhenIdExist() {
        Long existingId = 1L;
        when(exerciseRepository.findById(existingId)).thenReturn(Optional.of(exercise));

        Exercise result = exerciseService.findById(existingId);
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals("Supino", result.getName());

        verify(exerciseRepository, times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundExecption quando o ID nao existir")
    void findById_ShouldThrowException_WhenIdNotExist() {
        Long nonExistingId = 99L;
        when (exerciseRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.findById(nonExistingId);
        });
        verify(exerciseRepository, times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("Deve deletar o exercicio quando o ID existir e nao tiver dependencias")
    void delete_ShouldDoNothingWhenIdExist() {
        Long existingId = 1L;
        when (exerciseRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(exerciseRepository).deleteById(existingId);

        assertDoesNotThrow(() -> {
            exerciseService.delete(existingId);
        });

        verify(exerciseRepository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando tentar deletar o ID inexistente")
    void delete_ShouldThrowException_WhenIdNotExist() {
        Long nonExistingId = 99L;
        when (exerciseRepository.existsById(nonExistingId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.delete(nonExistingId);
        });
        verify(exerciseRepository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Deve lancar DataBaseException quando houver violacao de integridade")
    void delete_ShouldThrowDataBaseException() {
        Long dependentId = 1L;
        when (exerciseRepository.existsById(dependentId)).thenReturn(true);

        doThrow(DataIntegrityViolationException.class).when(exerciseRepository).deleteById(dependentId);
        assertThrows(DatabaseException.class, () -> {
            exerciseService.delete(dependentId);
        });

        verify(exerciseRepository, times(1)).deleteById(dependentId);
    }

    @Test
    @DisplayName("Deve atualizar e retornar o exercicio quando o ID existir")
    void update_ShouldReturnUpdatedExercise_WhenIdExist() {
        Long existingId = 1L;

        Exercise exerciseUpdate = new Exercise();
        exerciseUpdate.setName("Supino atualizado");
        exerciseUpdate.setMuscleGroup("Chest Atualizado");

        when(exerciseRepository.getReferenceById(existingId)).thenReturn(exercise);
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        Exercise result = exerciseService.update(existingId, exerciseUpdate);

        assertEquals("Supino atualizado", result.getName());
        assertEquals("Chest Atualizado", result.getMuscleGroup());

        verify(exerciseRepository, times(1)).getReferenceById(existingId);
        verify(exerciseRepository, times(1)).save(exercise);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando tentar atualizar")
    void update_ShouldThrowException_WhenIdNotExist() {
        Long nonExistingId = 99L;
        Exercise exerciseUpdate = new Exercise();
        exerciseUpdate.setName("Supino Errado");

        when(exerciseRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.update(nonExistingId, exerciseUpdate);
        });

        verify(exerciseRepository, times(1)).getReferenceById(nonExistingId);
        verify(exerciseRepository, never()).save(exercise);

    }

    @Test
    @DisplayName("Deve inserir e retornar um exercicio com sucesso")
    void insert_ShouldReturnExercise_WhenInsertIsSuccessful() {
        Exercise newExercise = new Exercise();
        newExercise.setName("Remada");
        newExercise.setMuscleGroup("Back");

        when(exerciseRepository.save(newExercise)).thenReturn(newExercise);

        Exercise result = exerciseService.insert(newExercise);

        assertNotNull(result);
        assertEquals("Remada", result.getName());
        assertEquals("Back", result.getMuscleGroup());

        verify(exerciseRepository, times(1)).save(newExercise);
    }

    @Test
    @DisplayName("Deve retornar um DataIntegrityViolationException caso o exercicio ja exista")
    void insert_ShouldThrowDataIntegrityViolationException_WhenExerciseAlreadyExists() {
        Exercise newExercise = new Exercise();
        newExercise.setName("Remada");
        newExercise.setMuscleGroup("Back");

        doThrow(DataIntegrityViolationException.class).when(exerciseRepository).save(newExercise);

        assertThrows(DataIntegrityViolationException.class, () -> {
            exerciseService.insert(newExercise);
        });

        verify(exerciseRepository, times(1)).save(newExercise);
    }

}
