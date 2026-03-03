package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.DTO.WorkoutSetDTO;
import com.pedro.ironlogapi.DTO.WorkoutSetRequestDTO;
import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.entities.User;
import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.entities.WorkoutSet;
import com.pedro.ironlogapi.repositories.ExerciseRepository;
import com.pedro.ironlogapi.repositories.WorkoutRepository;
import com.pedro.ironlogapi.repositories.WorkoutSetRepository;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutSetServiceTest {

    @InjectMocks
    private WorkoutSetService workoutSetService;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    private WorkoutSet workoutSet;

    private Exercise exercise = new Exercise(null, "Supino", "Peito");

    private User user = new User(null, "Pedro", "pedro@gmail.com", "1234");

    private Workout workout = new Workout(null, "Treino D", Instant.parse("2026-03-10T14:30:00Z"), user);

    @BeforeEach
    void setUp() {
        this.workoutSet = new WorkoutSet();
        workoutSet.setId(1L);
        workoutSet.setExercise(exercise);
        workoutSet.setWorkout(workout);
        workoutSet.setSets(3);
        workoutSet.setReps(12);
        workoutSet.setWeight(20.5);
    }

    @Test
    @DisplayName("Deve retornar uma lista de WorkoutSet")
    void findAll_ShouldReturnListOfWorkoutSet(){
        WorkoutSet workoutSet2 = new WorkoutSet();
        workoutSet2.setId(2L);
        workoutSet2.setExercise(exercise);
        workoutSet2.setWorkout(workout);
        workoutSet2.setSets(5);
        workoutSet2.setReps(20);
        workoutSet2.setWeight(32.0);

        when(workoutSetRepository.findAll()).thenReturn(Arrays.asList(workoutSet, workoutSet2));

        List<WorkoutSet> result = workoutSetService.getAllWorkoutSets();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(3, result.get(0).getSets());
        assertEquals(20, result.get(1).getReps());

        verify(workoutSetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando nao houver WorkoutSet")
    void findAll_ShouldReturnEmptyList_WhenWorkoutSetNotFound(){
        when(workoutSetRepository.findAll()).thenReturn(Collections.emptyList());

        List<WorkoutSet> result = workoutSetRepository.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(workoutSetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um set quando o ID existir")
    void findById_ShouldReturnWorkoutSet_WhenIdExists() {
        Long existingId = 1L;
        when(workoutSetRepository.findById(existingId)).thenReturn(Optional.of(workoutSet));

        WorkoutSet result = workoutSetService.getWorkoutSetById(existingId);

        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals("Supino", result.getExercise().getName());

        verify(workoutSetRepository, times(1)).findById(existingId);

    }

    @Test
    @DisplayName("Deve retornar um ResourceNotFoundException quando o ID nao existir")
    void findById_ShouldReturnResourceNotFoundException_WhenIdDoesNotExist() {
        Long nonExistingId = 99L;

        when(workoutSetRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutSetService.getWorkoutSetById(nonExistingId);
        });
        verify(workoutSetRepository, times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("Deve deletar um set quando o ID existir e nao tiver dependencias")
    void delete_ShouldDoNothingWhenIdExists() {
        Long existingId = 1L;

        when(workoutSetRepository.existsById(existingId)).thenReturn(true);

        doNothing().when(workoutSetRepository).deleteById(existingId);

        assertDoesNotThrow(() -> {
            workoutSetService.deleteWorkoutSetById(existingId);
        });
        verify(workoutSetRepository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Deve lancar um ResourceNotFoundException quando tentar deletar o ID inexistente")
    void delete_ShouldThrowException_WhenIdDoesNotExist() {
        Long nonExistingId = 99L;

        when(workoutSetRepository.existsById(nonExistingId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> {
            workoutSetService.deleteWorkoutSetById(nonExistingId);
        });

        verify(workoutSetRepository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Deve lancar um DataBaseException quando houver violacao de integridade")
    void delete_ShouldThrowDataBaseException() {
        Long existingId = 1L;

        when(workoutSetRepository.existsById(existingId)).thenReturn(true);

        doThrow(DatabaseException.class).when(workoutSetRepository).deleteById(existingId);

        assertThrows(DatabaseException.class, () -> {
            workoutSetService.deleteWorkoutSetById(existingId);
        });

        verify(workoutSetRepository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Deve atualizar e retornar um set quando o ID existir")
    void update_ShouldReturnUpdatedExercise_WhenIdExists() {
        Long existingId = 1L;
        Long newWorkoutId = 2L;
        Long newExerciseId = 2L;

        Exercise newExercise = new Exercise(newExerciseId, "Teste", "Teste");
        Workout newWorkout = new Workout(newWorkoutId, "Treino teste", Instant.parse("2026-03-10T14:30:00Z"), user);

        WorkoutSetRequestDTO workoutSetDTO = new WorkoutSetRequestDTO();
        workoutSetDTO.setSets(4);
        workoutSetDTO.setReps(15);
        workoutSetDTO.setWeight(100.0);
        workoutSetDTO.setWorkoutId(newWorkoutId);
        workoutSetDTO.setExerciseId(newExerciseId);

        when(workoutSetRepository.getReferenceById(existingId)).thenReturn(workoutSet);
        when(workoutRepository.findById(newWorkoutId)).thenReturn(Optional.of(newWorkout));
        when(exerciseRepository.findById(newExerciseId)).thenReturn(Optional.of(newExercise));
        when(workoutSetRepository.save(any(WorkoutSet.class))).thenReturn(workoutSet);

        WorkoutSet result = workoutSetService.update(existingId, workoutSetDTO);

        assertNotNull(result);
        assertEquals(4, result.getSets());
        assertEquals(15, result.getReps());
        assertEquals(100.0, result.getWeight());
        assertEquals(newExercise, result.getExercise());

        verify(workoutSetRepository, times(1)).getReferenceById(existingId);
        verify(workoutRepository, times(1)).findById(newWorkoutId);
        verify(exerciseRepository, times(1)).findById(newExerciseId);
        verify(workoutSetRepository, times(1)).save(workoutSet);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando tentar atualizar um WorkoutSet inexistente")
    void update_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
        Long nonExistingId = 99L;

        WorkoutSetRequestDTO workoutSetDTO = new WorkoutSetRequestDTO();

        when(workoutSetRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutSetService.update(nonExistingId, workoutSetDTO);
        });

        verify(workoutSetRepository, times(1)).getReferenceById(nonExistingId);
        verify(workoutRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando o Workout do DTO nao existir")
    void update_ShouldThrowResourceNotFoundException_WhenWorkoutIdInDtoDoesNotExist() {
        Long existingId = 1L;
        Long nonExistingWorkoutId = 99L;

        WorkoutSetRequestDTO workoutSetDTO = new WorkoutSetRequestDTO();
        workoutSetDTO.setWorkoutId(nonExistingWorkoutId);
        workoutSetDTO.setExerciseId(2L);

        when(workoutSetRepository.getReferenceById(existingId)).thenReturn(workoutSet);
        when(workoutRepository.findById(nonExistingWorkoutId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutSetService.update(existingId, workoutSetDTO);
        });

        verify(workoutSetRepository, times(1)).getReferenceById(existingId);
        verify(workoutRepository, times(1)).findById(nonExistingWorkoutId);

        verify(exerciseRepository, never()).findById(anyLong());
        verify(workoutSetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve inserir e retornar o WorkoutSet com sucesso")
    void insert_ShouldReturnWorkoutSet_WhenInsertIsSucess(){
        Long workoutId = 1L;
        Long exerciseId = 1L;

        WorkoutSetRequestDTO workoutSetDTO = new WorkoutSetRequestDTO();
        workoutSetDTO.setWorkoutId(workoutId);
        workoutSetDTO.setExerciseId(exerciseId);
        workoutSetDTO.setSets(3);
        workoutSetDTO.setReps(10);
        workoutSetDTO.setWeight(50.0);

        WorkoutSet savedWorkoutSet = new WorkoutSet(1L, 3, 10, 50.0, workout, exercise);

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(workoutSetRepository.save(any(WorkoutSet.class))).thenReturn(savedWorkoutSet);

        WorkoutSet result = workoutSetService.createWorkoutSet(workoutSetDTO);

        assertNotNull(result);
        assertEquals(3, result.getSets());
        assertEquals(10, result.getReps());
        assertEquals(50.0, result.getWeight());
        assertEquals(workout, result.getWorkout());
        assertEquals(exercise, result.getExercise());

        verify(workoutRepository, times(1)).findById(workoutId);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verify(workoutSetRepository, times(1)).save(any(WorkoutSet.class));
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando o Workout nao existir")
    void insert_ShouldThrowResourceNotFoundException_WhenWorkoutDtoDoesNotExist() {
        Long nonExistingWorkoutId = 99L;

        WorkoutSetRequestDTO workoutSetDTO = new WorkoutSetRequestDTO();
        workoutSetDTO.setWorkoutId(nonExistingWorkoutId);
        workoutSetDTO.setExerciseId(1L);

        when(workoutRepository.findById(nonExistingWorkoutId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutSetService.createWorkoutSet(workoutSetDTO);
        });

        verify(workoutRepository, times(1)).findById(nonExistingWorkoutId);

        verify(exerciseRepository, never()).findById(anyLong());
        verify(workoutSetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando o Exercise nao existir")
    void insert_ShouldThrowResourceNotFoundException_WhenExerciseDoesNotExist() {
        Long validWorkoutId = 1L;
        Long nonExistingExerciseId = 99L;

        WorkoutSetRequestDTO workoutSetDTO = new WorkoutSetRequestDTO();
        workoutSetDTO.setWorkoutId(validWorkoutId);
        workoutSetDTO.setExerciseId(nonExistingExerciseId);

        when(workoutRepository.findById(validWorkoutId)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findById(nonExistingExerciseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutSetService.createWorkoutSet(workoutSetDTO);
        });

        verify(workoutRepository, times(1)).findById(validWorkoutId);
        verify(exerciseRepository, times(1)).findById(nonExistingExerciseId);
        verify(workoutSetRepository, never()).save(any());

    }

}
