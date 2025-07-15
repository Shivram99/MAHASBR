package com.mahasbr.customanotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = MyCustomValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MyCustomValidation {

    String message() default "Invalid value"; // Default validation message

    Class<?>[] groups() default {}; // Groups for categorizing validations

    Class<? extends Payload>[] payload() default {}; // Payload for metadata

    // Add any additional fields for parameters if needed
}