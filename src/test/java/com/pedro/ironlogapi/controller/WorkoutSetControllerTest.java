package com.pedro.ironlogapi.controller;

import com.pedro.ironlogapi.DTO.WorkoutSetDTO;
import com.pedro.ironlogapi.DTO.WorkoutSetRequestDTO;
import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.entities.User;
import com.pedro.ironlogapi.entities.Workout;
import com.pedro.ironlogapi.entities.WorkoutSet;
import com.pedro.ironlogapi.security.SecurityFilter;
import com.pedro.ironlogapi.security.TokenService;
import com.pedro.ironlogapi.service.WorkoutSetService;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkoutSetController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class WorkoutSetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @MockitoBean
    private WorkoutSetService workoutSetService;

    private WorkoutSet workoutSet;

    private Workout workout;

    private Exercise exercise;

    private User user;

    @BeforeEach
    void setUp(){
        user = new User(1L, "Pedro", "pedro@gmail.com", "123456");
        workout = new Workout(1L, "Treino de Peito", Instant.now(), user);
        exercise = new Exercise(1L, "Supino Reto", "Peito");

        workoutSet = new WorkoutSet(1L, 3, 10, 60.0, workout, exercise);
    }

    @Test
    @DisplayName("Deve retornar 200 OK e uma Lista de WorkoutSets")
    void findAll_ShouldReturnListOfWorkoutSetAndStatus200() throws Exception {
        WorkoutSet workoutSet1 = new WorkoutSet(2L, 5, 20, 15.0, workout, exercise);

        when(workoutSetService.getAllWorkoutSets()).thenReturn(Arrays.asList(workoutSet, workoutSet1));

        mockMvc.perform(get("/workout-sets")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].sets").value(3))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].sets").value(5));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o WorkoutSet em JSON quando o ID existir")
    void findById_ShouldReturnWorkoutSetAndStatus200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        when(workoutSetService.getWorkoutSetById(existingId)).thenReturn(workoutSet);

        mockMvc.perform(get("/workout-sets/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sets").value(3))
                .andExpect(jsonPath("$.reps").value(10))
                .andExpect(jsonPath("$.weight").value(60.0));
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando o ID nao existir")
    void findById_ShouldReturn404_WhenIdNotExists() throws Exception {
        Long nonExistingId = 99L;

        when(workoutSetService.getWorkoutSetById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/workout-sets/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retonar 201 Created e o WorkoutSet quando a insercao for sucesso")
    void insert_ShouldReturn201_WhenInsertIsSucess() throws Exception {
        String jsonBody = """
                    {
                        "sets": 3,
                        "reps": 10,
                        "weight": 60.0,
                        "workout": { "id": 1},
                        "exercise": { "id": 1}
                    }
                """;
        when(workoutSetService.createWorkoutSet(any(WorkoutSetRequestDTO.class))).thenReturn(workoutSet);

        mockMvc.perform(post("/workout-sets")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sets").value(3))
                .andExpect(jsonPath("$.weight").value(60.0));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar inserir com Workout ou Exercise inexistente")
    void insert_ShouldReturn404_WhenInsertIsNotSucess() throws Exception {
        String jsonBody = """
                    {
                        "sets": 3,
                        "reps": 10,
                        "weight": 60.0,
                        "workoutId": 99,
                        "exerciseId": 99
                    }
                """;
        when(workoutSetService.createWorkoutSet(any(WorkoutSetRequestDTO.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(post("/workout-sets")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um 400 Bad Request quando houver erro de integridade")
    void insert_ShouldReturn400_WhenIntegrityViolation() throws Exception {
        String jsonBody = """
                    {
                        "sets": 3,
                        "reps": 10,
                        "weight": 60.0,
                        "workoutId": 1,
                        "exerciseId": 1
                    }
                """;
        when(workoutSetService.createWorkoutSet(any(WorkoutSetRequestDTO.class))).thenThrow(DatabaseException.class);

        mockMvc.perform(post("/workout-sets")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o WorkoutSet atualizado quando o ID existir")
    void update_ShouldReturn200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        WorkoutSet updatedSet = new WorkoutSet(existingId, 4, 10, 65.0, workoutSet.getWorkout(), workoutSet.getExercise());

        String jsonBody = """
                    {
                        "sets": 4,
                        "reps": 10,
                        "weight": 65.0,
                        "workout": { "id": 1},
                        "exercise": { "id": 1}
                    }
                """;
        when(workoutSetService.update(eq(existingId), any(WorkoutSetRequestDTO.class))).thenReturn(updatedSet);

        mockMvc.perform(put("/workout-sets/{id}", existingId)
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sets").value(4))
                .andExpect(jsonPath("$.weight").value(65.0));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando tentar atualizar um ID inexistente")
    void update_ShouldReturn404_WhenIdNotExists() throws Exception {
        Long nonExistingId = 99L;

        String jsonBody = """
                    {
                        "sets": 3,
                        "reps": 10,
                        "weight": 60.0,
                        "workoutId": 1,
                        "exerciseId": 1
                    }
                """;
        when(workoutSetService.update(eq(nonExistingId), any(WorkoutSetRequestDTO.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/workout-sets/{id}", nonExistingId)
                            .content(jsonBody)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 204 No Content quando o ID existir e for deletado")
    void delete_ShouldReturn204_WhenIdExists() throws Exception {
        Long existingId = 1L;

        doNothing().when(workoutSetService).deleteWorkoutSetById(existingId);

        mockMvc.perform(delete("/workout-sets/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando tentar deletar um ID inexistente")
    void delete_ShouldReturn404_WhenIdNotExists() throws Exception {
        Long nonExistingId = 99L;

        doThrow(ResourceNotFoundException.class).when(workoutSetService).deleteWorkoutSetById(nonExistingId);

        mockMvc.perform(delete("/workout-sets/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando houver violacao de integridade")
    void delete_ShouldReturn400_WhenIntegrityViolation() throws Exception {
        Long dependentId = 1L;

        doThrow(DatabaseException.class).when(workoutSetService).deleteWorkoutSetById(dependentId);

        mockMvc.perform(delete("/workout-sets/{id}", dependentId))
                .andExpect(status().isBadRequest());
    }

}
