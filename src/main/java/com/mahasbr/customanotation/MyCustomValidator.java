package com.mahasbr.customanotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MyCustomValidator implements ConstraintValidator<MyCustomValidation, String> {

    @Override
    public void initialize(MyCustomValidation constraintAnnotation) {
        // Initialization code if needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Add your custom validation logic here
        if (value == null || value.isEmpty()) {
            return false; // Invalid if value is null or empty
        }

        // Example logic: value must contain the word "valid"
        return value.contains("valid");
    }
}
