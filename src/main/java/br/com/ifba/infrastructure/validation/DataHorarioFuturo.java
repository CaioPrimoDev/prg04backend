package br.com.ifba.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // <-- Aplicada à CLASSE
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DataHorarioFuturoValidator.class) // <-- Liga ao validador
public @interface DataHorarioFuturo {

    String message() default "A data e/ou horário deve ser no futuro.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}