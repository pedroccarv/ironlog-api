package com.pedro.ironlogapi.controller;

import com.pedro.ironlogapi.entities.Exercise;
import com.pedro.ironlogapi.security.SecurityFilter;
import com.pedro.ironlogapi.security.TokenService;
import com.pedro.ironlogapi.service.ExerciseService;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ExerciseController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @MockitoBean
    private ExerciseService exerciseService;

    private Exercise exercise;

    @BeforeEach
    void setUp(){
        exercise = new Exercise();
        exercise.setId(1L);
        exercise.setName("Teste");
        exercise.setMuscleGroup("Testado");
    }

    @Test
    @DisplayName("Deve retornar 200 OK e uma Lista de Exercicios")
    void findAll_ShouldReturnListOfUsersAndStatus200() throws Exception {
        Exercise exercise1 = new Exercise(2L, "Exercicio", "Exercicio Teste");

        when(exerciseService.findAll()).thenReturn(List.of(exercise, exercise1));

        mockMvc.perform(get("/exercises")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Teste"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Exercicio"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o Exercise em Json")
    void findById_ShouldReturnExerciseAndStatus200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        when(exerciseService.findById(existingId)).thenReturn(exercise);

        mockMvc.perform(get("/exercises/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teste"))
                .andExpect(jsonPath("$.muscleGroup").value("Testado"));
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando o ID nao existir")
    void findById_ShouldReturn404_WhenIdNotExists() throws Exception {
        Long nonExistingId = 99L;

        when(exerciseService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/exercises/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um 204 No Content quando o ID existir e for deletado")
    void deleteById_ShouldReturn204_WhenIdExists() throws Exception {
        Long existingId = 1L;

        doNothing().when(exerciseService).delete(existingId);

        mockMvc.perform(delete("/exercises/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando tentar deletar um ID inexistente")
    void delete_ShouldReturn404_WhenIdNotExists() throws Exception {
        Long nonExistingId = 99L;

        doThrow(ResourceNotFoundException.class).when(exerciseService).delete(nonExistingId);

        mockMvc.perform(delete("/exercises/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um 400 Bad Request quando houver violacao de integridade no delete")
    void delete_ShouldReturn400_WhenIntegrityViolation() throws Exception {
        long dependentId = 1L;

        doThrow(DatabaseException.class).when(exerciseService).delete(dependentId);

        mockMvc.perform(delete("/exercises/{id}", dependentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar um 200 OK e o Exercicio atualizado quando o ID existir")
    void updateById_ShouldReturn200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        Exercise updatedExercise = new Exercise(null, "Exercicio Novo", "Grupo Novo");

        when(exerciseService.update(eq(existingId), any(Exercise.class))).thenReturn(updatedExercise);

        String jsonBody = """
                {
                    "name": "Exercicio Novo",
                    "muscleGroup": "Grupo Novo"
                }
                """;
        mockMvc.perform(put("/exercises/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Exercicio Novo"))
                .andExpect(jsonPath("$.muscleGroup").value("Grupo Novo"));
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando tentar atualizar um ID inexistente")
    void update_ShouldReturn404_WhenIdNotExists() throws Exception {
        Long nonExistingId = 99L;

        String jsonBody = """
                {
                    "name": "Exercicio Errado",
                    "muscleGroup": "Grupo Errado"
                }
                """;
        when(exerciseService.update(eq(nonExistingId), any(Exercise.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/exercises/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 201 created e o Exercicio quando a insercao for sucesso")
    void insert_ShouldReturn201_WhenInsertIsSuccessful() throws Exception {
        String jsonBody = """
                {
                    "name": "Exercicio Novo",
                    "muscleGroup": "Grupo Novo"
                }
                """;
        Exercise newExercise = new Exercise(2L, "Exercicio Novo", "Grupo Novo");

        when(exerciseService.insert(any(Exercise.class))).thenReturn(newExercise);

        mockMvc.perform(post("/exercises")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Exercicio Novo"))
                .andExpect(jsonPath("$.muscleGroup").value("Grupo Novo"));
    }

    @Test
    @DisplayName("Deve retornar um 400 Bad Request quando houver erro de integridade")
    void insert_ShouldReturn400_WhenIntegrityViolation() throws Exception {
        String jsonBody = """
                {
                    "name": "Exercicio Erro",
                    "muscleGroup": "Grupo Erro"
                }
                """;
        when(exerciseService.insert(any(Exercise.class))).thenThrow(DatabaseException.class);

        mockMvc.perform(post("/exercises")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}