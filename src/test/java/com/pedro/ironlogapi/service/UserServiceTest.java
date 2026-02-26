package com.pedro.ironlogapi.service;

import com.pedro.ironlogapi.entities.User;
import com.pedro.ironlogapi.repositories.UserRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Pedro");
        existingUser.setEmail("pedro@gmail.com");
    }

    @Test
    @DisplayName("Deve retornar um User quando o ID existir")
    void findById_ShouldReturnUser_WhenIdExist() {
        Long existingId = 1L;
        when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));

        User result = userService.findById(existingId);
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals("Pedro", result.getName());

        verify(userRepository, times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando o ID nao existir")
    void findById_ShouldThrowResourceNotFoundException_WhenIdNotExist() {
        Long existingId = 99L;
        when(userRepository.findById(existingId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findById(existingId);
        });
        verify(userRepository, times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Deve deletar o usuario quando o ID existir e nao houver dependencias")
    void delete_ShouldDoNothing_WhenIdExist() {
        Long existingId = 1L;
        when(userRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(existingId);

        userService.delete(existingId);
        verify(userRepository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando tentar deletar ID inexistente")
    void delete_ShouldThrowResourceNotFoundException_WhenIdNotExist() {
        Long existingId = 99L;
        when(userRepository.existsById(existingId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.delete(existingId);
        });

        verify(userRepository, never()).deleteById(existingId);
    }

    @Test
    @DisplayName("Deve lancar DataBaseException quando houver violacao de integridade")
    void delete_ShouldThrowDataBaseException_WhenDependentId() {
        Long dependentId = 1L;

        when(userRepository.existsById(dependentId)).thenReturn(true);

        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(dependentId);

        assertThrows(DatabaseException.class, () -> {
            userService.delete(dependentId);
        });
        verify(userRepository, times(1)).deleteById(dependentId);
    }

    @Test
    @DisplayName("Deve atualizar e retornar o usuario quando o ID existir")
    void update_ShouldReturnUpdatedUser_WhenIdExist() {
        Long existingId = 1L;

        User updateData = new User();
        updateData.setName("Pedro atualizado");
        updateData.setEmail("pedro.novo@gmail.com");
        when(userRepository.getReferenceById(existingId)).thenReturn(existingUser);

        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.update(existingId, updateData);

        assertNotNull(result);
        assertEquals("Pedro atualizado", result.getName());
        assertEquals("pedro.novo@gmail.com", result.getEmail());

        verify(userRepository, times(1)).getReferenceById(existingId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando tentar atualizar")
    void update_ShouldThrowResourceNotFoundException_WhenIdNotExist() {
        Long nonExistingId = 99L;
        User updateData = new User();
        updateData.setName("Pedro Errado");
        when(userRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.update(nonExistingId, updateData);
        });
        verify(userRepository, times(1)).getReferenceById(nonExistingId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve inserir e retornar o usuario com sucesso")
    void insert_ShouldReturnCreatedUser_WhenIdExist() {
        User newUser = new User();
        newUser.setName("Novo Usuario");
        newUser.setEmail("novo@gmail.com");

        when(userRepository.save((any(User.class)))).thenReturn(newUser);

        User result = userService.insert(newUser);

        assertNotNull(result);
        assertEquals("Novo Usuario", result.getName());
        assertEquals("novo@gmail.com", result.getEmail());

        verify(userRepository, times(1)).save(newUser);

    }

}
