package com.babydocs.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint( validatedBy = EnumValidator.class )
@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
public @interface ValidEnum
{
    Class<? extends Enum<?>> targetClassType();

    String message() default "Value is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
