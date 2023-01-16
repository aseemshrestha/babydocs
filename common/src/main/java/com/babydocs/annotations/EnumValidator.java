package com.babydocs.annotations;


import com.babydocs.Constants;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class EnumValidator implements ConstraintValidator<EnumValidation, String> {
    public boolean isValid(String gender, ConstraintValidatorContext cxt) {
        List<String> list = Arrays.asList(Constants.Gender.MALE.toString(), Constants.Gender.FEMALE.toString());
        return list.contains(gender);
    }
}