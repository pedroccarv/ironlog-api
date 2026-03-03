package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.entities.User;
import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.entities.WorkoutSet;
import com.pedro.ironlogapi.repositories.WorkoutRepository;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {

    @InjectMocks
    private WorkoutService workoutService;

    @Mock
    private WorkoutRepository workoutRepository;

    private Workout workout;

    private User user = new User(null, "Pedro", "pedro@gmail.com", "1234");

    private Exercise exercise = new Exercise(null , "Supino", "Chest");

    private Exercise exercise2 = new Exercise(null, "High row", "Back");

    private WorkoutSet workoutSet = new WorkoutSet(null, 3, 12, 20.0, null, exercise);
    private WorkoutSet workoutSet2 = new WorkoutSet(null, 3, 12, 50.0, null, exercise2);

    @BeforeEach
    public void setUp() {
        workout = new Workout();
        workout.setId(1L);
        workout.setTitle("Treino B");
        workout.setDate(Instant.parse("2026-03-10T14:30:00Z"));
        workout.setSets(List.of(workoutSet, workoutSet2));
        workout.setUser(user);
        workoutSet.setWorkout(workout);
        workoutSet2.setWorkout(workout);
    }

    @Test
    @DisplayName("Deve retornar uma lista de Workouts")
    void findAll_ShouldReturnListOfWorkouts() {
        Workout workout2 = new Workout();
        workout2.setId(2L);
        workout2.setTitle("Treino D");
        workout2.setDate(Instant.now());
        workout2.setSets(List.of(workoutSet, workoutSet2));

        when(workoutRepository.findAll()).thenReturn(Arrays.asList(workout, workout2));

        List<Workout> result = workoutService.getAllWorkouts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Treino B", result.get(0).getTitle());
        assertEquals("Treino D", result.get(1).getTitle());

        verify(workoutRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando nao houver workouts")
    void findAll_ShouldReturnEmptyList_WhenWorkoutNotFound() {
        when(workoutRepository.findAll()).thenReturn(Collections.emptyList());

        List<Workout> result = workoutRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(workoutRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um treino quado o ID existir")
    void findById_ShouldReturnWorkout_WhenIdExist() {
        Long existingId = 1L;
        when(workoutRepository.findById(existingId)).thenReturn(Optional.of(workout));

        Workout result = workoutService.getWorkoutById(existingId);

        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals("Treino B", result.getTitle());

        verify(workoutRepository, times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando o ID nao existir")
    void findById_ShouldReturnResourceNotFoundException_WhenIdNotExist() {
        Long nonExistingId = 99L;
        when (workoutRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.getWorkoutById(nonExistingId);
        });

        verify(workoutRepository, times(1)).findById(nonExistingId);

    }

    @Test
    @DisplayName("Deve deletar o treino quando o ID existir e nao tiver dependencias")
    void delete_ShouldDoNothingWhenIdExist() {
        Long existingId = 1L;
        when (workoutRepository.existsById(existingId)).thenReturn(true);

        doNothing().when(workoutRepository).deleteById(existingId);

        assertDoesNotThrow(() -> {
            workoutService.delete(existingId);
        });

        verify(workoutRepository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando tentar deletar o ID inexistente")
    void delete_ShouldReturnResourceNotFoundException_WhenIdNotExist() {
        Long nonExistingId = 99L;

        when (workoutRepository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.delete(nonExistingId);
        });

        verify(workoutRepository, never()).deleteById(nonExistingId);

    }

    @Test
    @DisplayName("Deve lancar DataBaseException quando houver violacao de integridade")
    void delete_ShouldThrowDataBaseException() {
        Long dependentId = 1L;

        when(workoutRepository.existsById(dependentId)).thenReturn(true);

        doThrow(DataIntegrityViolationException.class).when(workoutRepository).deleteById(dependentId);

        assertThrows(DatabaseException.class, () -> {
            workoutService.delete(dependentId);
        });

        verify(workoutRepository, times(1)).deleteById(dependentId);
    }

    @Test
    @DisplayName("Deve atualizar e retornar o treino quando o ID existir")
    void update_ShouldReturnUpdatedWorkout_WhenIdExist() {
        Long existingId = 1L;

        Workout workoutUpdate = new Workout();
        WorkoutSet workoutSet = new WorkoutSet(null, 3, 12, 25.0, workout, exercise2);
        WorkoutSet workoutSet2 = new WorkoutSet(null, 3, 12, 50.0, workout, exercise);

        List<WorkoutSet> workoutSets = List.of(workoutSet, workoutSet2);

        workoutUpdate.setTitle("Treino C");
        workoutUpdate.setDate(Instant.parse("2026-03-10T14:30:00Z"));
        workoutUpdate.setSets(Arrays.asList(workoutSet, workoutSet2));

        when(workoutRepository.getReferenceById(existingId)).thenReturn(workout);
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);

        Workout result = workoutService.updateWorkout(existingId, workoutUpdate);

        assertEquals("Treino C", result.getTitle());
        assertEquals(Instant.parse("2026-03-10T14:30:00Z"), result.getDate());
        assertEquals(workoutSets, result.getSets());

        verify(workoutRepository, times(1)).getReferenceById(existingId);
        verify(workoutRepository, times(1)).save(workout);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFounException quando tentar atualizar")
    void update_ShouldThrowResourceNotFounException_WhenIdNotExist() {
        Long nonExistingId = 99L;
        Workout workoutUpdate = new Workout();
        workoutUpdate.setTitle("Treino errado");

        when(workoutRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            workoutService.updateWorkout(nonExistingId, workoutUpdate);
        });

        verify(workoutRepository, times(1)).getReferenceById(nonExistingId);
        verify(workoutRepository, never()).save(workout);
    }

    @Test
    @DisplayName("Deve inserir e retornar um treino com sucesso")
    void insert_ShouldReturnWorkout_WhenInsertIsSucess() {
        User user = new User(null, "teste", "teste@gmail.com", "teste");
        Exercise exercise = new Exercise(null, "Leg press", "Teste");
        Workout newWorkout = new Workout();
        WorkoutSet workoutSet = new WorkoutSet(null, 3, 12, 100.0, null, exercise);
        newWorkout.setTitle("Treino Teste");
        newWorkout.setUser(user);
        newWorkout.setDate(Instant.parse("2026-03-10T14:30:00Z"));
        newWorkout.setSets(List.of(workoutSet));
        workoutSet.setWorkout(newWorkout);

        List<WorkoutSet> workoutSetList = List.of(workoutSet);

        when (workoutRepository.save(newWorkout)).thenReturn(newWorkout);

        Workout result = workoutService.createWorkout(newWorkout);

        assertNotNull(result);
        assertEquals("Treino Teste", result.getTitle());
        assertEquals(user, result.getUser());
        assertEquals(Instant.parse("2026-03-10T14:30:00Z"), result.getDate());
        assertEquals(workoutSetList, result.getSets());

        verify(workoutRepository, times(1)).save(newWorkout);

    }

    @Test
    @DisplayName("Deve retornar um DataIntegrityViolationException caso o treino ja exista")
    void insert_ShouldThrowDataIntegrityViolationException_WhenWorkoutAlreadyExists() {
        Workout newWorkout = new Workout();
        newWorkout.setTitle("Treino TESTE");
        newWorkout.setDate(Instant.parse("2026-03-10T14:30:00Z"));

        doThrow(DataIntegrityViolationException.class).when(workoutRepository).save(newWorkout);

        assertThrows(DataIntegrityViolationException.class, () -> {
            workoutService.createWorkout(newWorkout);
        });

        verify(workoutRepository, times(1)).save(newWorkout);
    }
}
