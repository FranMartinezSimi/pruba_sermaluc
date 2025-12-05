package com.sermaluc.prueba_tecnica.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO para teléfono del usuario")
public class PhonesDTO {
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[0-9]{7,15}$", message = "Phone number must contain only digits and be between 7 and 15 characters")
    @Schema(description = "Número de teléfono", example = "987654321")
    private String number;

    @NotBlank(message = "City code cannot be blank")
    @Pattern(regexp = "^[0-9]{1,4}$", message = "City code must contain only digits and be between 1 and 4 characters")
    @Schema(description = "Código de ciudad", example = "2")
    private String cityCode;

    @NotBlank(message = "Country code cannot be blank")
    @Pattern(regexp = "^[0-9]{1,4}$", message = "Country code must contain 1 to 4 digits")
    @Schema(description = "Código de país", example = "56")
    private String countryCode;
}
