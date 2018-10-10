/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.json.constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.LocalDateTime;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

//Constraint annotation: checks if a LocalDateTime value is in the future (isValid returns true if it's in the future)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LDTInFuture.Validator.class})
public @interface LDTInFuture {

  String message() default "LocalDateTime must be in the future";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<LDTInFuture, LocalDateTime> {

    @Override
    public void initialize(final LDTInFuture LDTInFuture) {
    }

    @Override
    public boolean isValid(final LocalDateTime ldt, final ConstraintValidatorContext constraintValidatorContext) {
      if (ldt == null) {
        return true;
      }
      return ldt.isAfter(LocalDateTime.now());
    }
  }
}
