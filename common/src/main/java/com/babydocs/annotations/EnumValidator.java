package com.babydocs.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class EnumValidator implements ConstraintValidator<ValidEnum, String>
{
    private final Set<String> allowed = new HashSet<>();

    public void initialize(ValidEnum targetEnum)
    {
        Class<? extends Enum<?>> selected = targetEnum.targetClassType();
        Enum<?>[] enumConstants = selected.getEnumConstants();
        for (Enum e : enumConstants) {
            allowed.add(e.toString().toUpperCase());
        }
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext)
    {
        return allowed.contains(s.toUpperCase());
    }
}
