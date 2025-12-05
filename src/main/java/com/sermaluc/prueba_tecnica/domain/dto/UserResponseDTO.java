package com.sermaluc.prueba_tecnica.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta al crear un usuario exitosamente")
public class UserResponseDTO {
    @Schema(description = "ID único del usuario", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Fecha de creación del usuario", example = "2025-12-05T10:30:00")
    private LocalDateTime created;

    @Schema(description = "Fecha de última modificación", example = "2025-12-05T10:30:00")
    private LocalDateTime modified;

    @Schema(description = "Fecha del último login", example = "2025-12-05T10:30:00")
    private LocalDateTime lastLogin;

    @Schema(description = "Token de sesión del usuario", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String token;

    @Schema(description = "Estado de activación del usuario", example = "true")
    private Boolean isActive;
}
