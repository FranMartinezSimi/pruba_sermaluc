package com.sermaluc.prueba_tecnica.infraestructure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sermaluc.prueba_tecnica.domain.dto.UserDTO;
import com.sermaluc.prueba_tecnica.domain.dto.UserResponseDTO;
import com.sermaluc.prueba_tecnica.domain.models.UserModel;
import com.sermaluc.prueba_tecnica.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    @Operation(summary = "Crear nuevo usuario",
            description = "Crea un nuevo usuario con validación de email único, formato de teléfono y encriptación de contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserModel savedUser = userService.saveUser(userDTO);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(savedUser.getId());
        response.setCreated(savedUser.getCreatedAt());
        response.setModified(savedUser.getModifiedAt());
        response.setLastLogin(savedUser.getLastLogin());
        response.setToken(savedUser.getToken());
        response.setIsActive(savedUser.getIsActive());

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }
}
