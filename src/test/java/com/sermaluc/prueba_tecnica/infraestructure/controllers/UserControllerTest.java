package com.sermaluc.prueba_tecnica.infraestructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sermaluc.prueba_tecnica.domain.dto.PhonesDTO;
import com.sermaluc.prueba_tecnica.domain.dto.UserDTO;
import com.sermaluc.prueba_tecnica.domain.exceptions.DuplicateEmailException;
import com.sermaluc.prueba_tecnica.domain.models.PhonesModel;
import com.sermaluc.prueba_tecnica.domain.models.UserModel;
import com.sermaluc.prueba_tecnica.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;
    private UserModel userModel;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new com.sermaluc.prueba_tecnica.infraestructure.exceptions.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        PhonesDTO phone = new PhonesDTO();
        phone.setNumber("987654321");
        phone.setCityCode("2");
        phone.setCountryCode("56");

        userDTO = new UserDTO();
        userDTO.setName("Juan Test");
        userDTO.setEmail("juan@test.com");
        userDTO.setPassword("Test123@");
        userDTO.setPhones(List.of(phone));

        PhonesModel phoneModel = new PhonesModel();
        phoneModel.setNumber("987654321");
        phoneModel.setCityCode("2");
        phoneModel.setCountryCode("56");

        userModel = new UserModel();
        userModel.setId(UUID.randomUUID());
        userModel.setName("Juan Test");
        userModel.setEmail("juan@test.com");
        userModel.setPassword("encodedPassword");
        userModel.setPhones(List.of(phoneModel));
        userModel.setCreatedAt(LocalDateTime.now());
        userModel.setModifiedAt(LocalDateTime.now());
        userModel.setLastLogin(LocalDateTime.now());
        userModel.setToken("jwt.token.here");
        userModel.setIsActive(true);
    }

    @Test
    void createUser_WithValidData_Returns201() throws Exception {
        when(userService.saveUser(any(UserDTO.class))).thenReturn(userModel);

        mockMvc.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.modified").exists())
                .andExpect(jsonPath("$.lastLogin").exists());
    }

    @Test
    void createUser_WithDuplicateEmail_Returns400() throws Exception {
        when(userService.saveUser(any(UserDTO.class)))
            .thenThrow(new DuplicateEmailException("El correo ya está registrado"));

        mockMvc.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El correo ya está registrado"));
    }
}
