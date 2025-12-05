package com.sermaluc.prueba_tecnica.domain.dto;

import java.util.List;

import com.sermaluc.prueba_tecnica.domain.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO para crear un nuevo usuario")
public class UserDTO {
    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Formato de email inválido")
    @Schema(description = "Email del usuario (debe ser único)", example = "juan@example.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @ValidPassword
    @Schema(description = "Contraseña (mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial)", example = "Pass123@")
    private String password;

    @NotEmpty(message = "Phones list cannot be empty")
    @Valid
    @Schema(description = "Lista de teléfonos del usuario")
    private List<PhonesDTO> phones;
}
