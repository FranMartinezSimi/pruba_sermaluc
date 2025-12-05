package com.sermaluc.prueba_tecnica.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidation.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Formato de contraseña inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
