package com.pedro.ironlogapi.controller;

import com.pedro.ironlogapi.entities.User;
import com.pedro.ironlogapi.security.SecurityFilter;
import com.pedro.ironlogapi.security.TokenService;
import com.pedro.ironlogapi.service.UserService;
import com.pedro.ironlogapi.service.exceptions.DatabaseException;
import com.pedro.ironlogapi.service.exceptions.ResourceNotFoundException;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private SecurityFilter securityFilter;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Teste");
        user.setEmail("teste@gmail.com");
        user.setPassword("123456");
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o User em JSON quando o ID existir")
    void findById_ShouldReturnUserAndStatus200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        when(userService.findById(existingId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teste"))
                .andExpect(jsonPath("$.email").value("teste@gmail.com"));
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando o ID nao existir")
    void findById_ShouldReturn404_WhenIdDoesNotExists() throws Exception {
        Long nonExistingId = 99L;

        when(userService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/users/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um 204 No Content quando o ID existir e for deletado")
    void delete_ShouldReturn204_WhenIdExists() throws Exception {
        Long existingId = 1L;

        doNothing().when(userService).delete(existingId);

        mockMvc.perform(delete("/users/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar um 400 Bad Request quando tentar deletar um ID inexistente")
    void delete_ShouldReturn400_WhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 99L;

        doThrow(ResourceNotFoundException.class).when(userService).delete(nonExistingId);

        mockMvc.perform(delete("/users/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar um 404 Bad Request quando houver violacao de integridade")
    void delete_ShouldReturn404_WhenIdIntegrityViolation() throws Exception {
        Long dependentId = 1L;

        doThrow(DatabaseException.class).when(userService).delete(dependentId);

        mockMvc.perform(delete("/users/{id}", dependentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar um 200 OK e o User atualizado quando o ID existir")
    void update_ShouldReturn200_WhenIdExists() throws Exception {
        Long existingId = 1L;

        User updatedUser = new User(null, "Usuario Atualizado", "testeA@gmail.com", "0123");

        when(userService.update(eq(existingId), any(User.class))).thenReturn(updatedUser);

        String jsonBody = """
                {
                    "name": "Usuario Atualizado",
                    "email": "testeA@gmail.com"
                }
                """;
        mockMvc.perform(put("/users/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Usuario Atualizado"))
                .andExpect(jsonPath("$.email").value("testeA@gmail.com"));
    }

    @Test
    @DisplayName("Deve retornar um 404 Not Found quando tentar atualizar um ID inexistente")
    void update_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
        Long nonExistingId = 99L;

        String jsonBody = """
                {
                    "name": "Usuario Errado",
                    "email": "email@errado.com"
                }
                """;
        when(userService.update(eq(nonExistingId), any(User.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/users/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 201 created e o User quando a insercao for sucesso")
    void insert_ShouldReturn201_WhenIdExists() throws Exception {
        String jsonBody = """
                {
                    "name": "Usuario Novo",
                    "email": "usuario@gmail.com",
                    "password": "123456"
                }
                """;
        User newUser = new User(2L, "Usuario Novo", "usuario@gmail.com", "123456");

        when(userService.insert(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/users")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Usuario Novo"))
                .andExpect(jsonPath("$.email").value("usuario@gmail.com"));
    }

    @Test
    @DisplayName("Deve retornar um 400 Bad Request quando houver erro de integridade")
    void insert_ShouldReturn400_WhenIntegrityViolation() throws Exception {
        String jsonBody = """
                {
                    "name": "Usuario Erro",
                    "email": "email@erro.com",
                    "password": "123456"
                }
                """;

        when(userService.insert(any(User.class))).thenThrow(DatabaseException.class);

        mockMvc.perform(post("/users")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
