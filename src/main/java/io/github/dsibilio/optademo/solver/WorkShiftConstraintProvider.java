package io.github.dsibilio.optademo.solver;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WorkShiftConstraintProvider implements ConstraintProvider {

  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    return Arrays.stream(WorkShiftConstraint.values())
        .filter(WorkShiftConstraint::isEnabled)
        .map(WorkShiftConstraint::getConstraintSpecifier)
        .map(constraintSpecifier -> constraintSpecifier.apply(constraintFactory))
        .toArray(Constraint[]::new);
  }

}
