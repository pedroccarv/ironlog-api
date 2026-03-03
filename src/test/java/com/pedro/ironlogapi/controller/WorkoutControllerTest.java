package com.pedro.ironlogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pedro.ironlogapi.entities.User;
import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.security.SecurityFilter;
import com.pedro.ironlogapi.security.TokenService;
import com.pedro.ironlogapi.service.WorkoutService;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = WorkoutController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private SecurityFilter securityFilter;

    private Workout workout = new Workout();

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Teste", "teste@gmail.com", "123456");
        workout = new Workout(1L, "Treino teste", Instant.parse("2026-03-10T14:30:00Z"), user);
    }

    @Test
    @DisplayName("Deve retornar 200 OK e uma Lista de Workouts")
    void findAll_ShouldReturnListOfWorkoutsAndStatus200() throws Exception {
        Workout workout1 = new Workout(2L, "Treino Lista", Instant.now(), null);

        when(workoutService.getAllWorkouts()).thenReturn(List.of(workout, workout1));

        mockMvc.perform(get("/workouts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Treino teste"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Treino Lista"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o Workout em JSON quando o ID existir")
    void findById_ShouldReturnWorkoutAndStatus200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        when(workoutService.getWorkoutById(existingId)).thenReturn(workout);

        mockMvc.perform(get("/workouts/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.title").value("Treino teste"))
                .andExpect(jsonPath("$.date").value("2026-03-10T14:30:00Z"));
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando o ID nao existir")
    void findById_ShouldReturn404_WhenIdDoesNotExists() throws Exception {
        Long nonExistingId = 99L;

        when(workoutService.getWorkoutById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/workouts/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um 204 No Content quando o ID existir e for deletado")
    void delete_ShouldReturn204_WhenIdExists() throws Exception {
        Long existingId = 1L;

        doNothing().when(workoutService).delete(existingId);

        mockMvc.perform(delete("/workouts/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando tentar deletar um ID inexistente")
    void delete_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 99L;

        doThrow(ResourceNotFoundException.class).when(workoutService).delete(nonExistingId);

        mockMvc.perform(delete("/workouts/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um 404 Bad Request quando houver violacao de integridade")
    void delete_ShouldReturn404_WhenIntegrityViolation() throws Exception {
        Long dependentId = 1L;

        doThrow(DatabaseException.class).when(workoutService).delete(dependentId);

        mockMvc.perform(delete("/workouts/{id}", dependentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o Workout atualizado quando o ID existir")
    void update_ShouldReturn200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        Workout updatedWorkout = new Workout(existingId, "Treino C - Atualizado", Instant.parse("2026-03-10T14:30:00Z"), workout.getUser());

        when(workoutService.updateWorkout(eq(existingId), any(Workout.class))).thenReturn(updatedWorkout);

        String jsonBody = """
                {
                    "title": "Treino C - Atualizado",
                    "date": "2026-03-10T14:30:00Z",
                    "user": {
                        "id": 1
                    }
                }
                """;

        mockMvc.perform(put("/workouts/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.title").value("Treino C - Atualizado"));
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando tentar atualizar um ID inexistente")
    void update_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 99L;

        String jsonBody = """
                {
                    "title": "Treino Errado",
                    "date": "2026-03-10T14:30:00Z"
                }
                """;

        when(workoutService.updateWorkout(eq(nonExistingId), any(Workout.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/workouts/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 201 Created e o Workout quando a insercao for sucesso")
    void insert_ShouldReturn201_WhenIdExists() throws Exception {
        String jsonBody = """
                {
                    "title": "Treino Novo",
                    "date" : "2026-03-15T10:30:00Z",
                    "user": {
                        "id": 1
                    }
                }
                """;
        Workout newWorkout = new Workout(2L, "Treino Novo", Instant.parse("2026-03-15T10:30:00Z"), workout.getUser());

        when(workoutService.createWorkout(any(Workout.class))).thenReturn(newWorkout);

        mockMvc.perform(post("/workouts")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Treino Novo"));
    }

    @Test
    @DisplayName("Deve retornar um 404 Bad Request quando houver erro de integridade")
    void insert_ShouldReturn404_WhenIntegrityViolation() throws Exception {
        String jsonBody = """
                {
                    "title": "Treino Novo",
                    "date": "2026-03-15T10:30:00Z",
                }
                """;

        when(workoutService.createWorkout(any(Workout.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/workouts")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
