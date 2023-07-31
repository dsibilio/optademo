package io.github.dsibilio.optademo.solver;

import io.github.dsibilio.optademo.domain.Employee;
import io.github.dsibilio.optademo.domain.WorkShift;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.Joiners;

import java.time.DayOfWeek;
import java.util.List;
import java.util.function.Function;

public enum WorkShiftConstraint {
  MAX_2_SHIFTS(WorkShiftConstraint::noMoreThanTwoShifts),
  JOE_PREFERS_TUESDAYS(WorkShiftConstraint::joePrefersTuesdays),
  AVOID_CONSECUTIVE_SHIFTS(WorkShiftConstraint::preferNonConsecutiveShifts);

  private final Function<ConstraintFactory, Constraint> constraintSpecifier;
  private boolean isEnabled = true;

  WorkShiftConstraint(Function<ConstraintFactory, Constraint> constraintSpecifier) {
    this.constraintSpecifier = constraintSpecifier;
  }

  private static Constraint noMoreThanTwoShifts(ConstraintFactory constraintFactory) {
    return constraintFactory.forEach(WorkShift.class)
        .join(Employee.class, Joiners.equal(WorkShift::getAssignedEmployee, Function.identity()))
        .groupBy((shift, employee) -> employee, ConstraintCollectors.countBi())
        .filter((employee, count) -> count > 2)
        .penalize(HardSoftScore.ONE_HARD)
        .asConstraint("More than 2 shifts");
  }

  private static Constraint joePrefersTuesdays(ConstraintFactory constraintFactory) {
    return constraintFactory.forEach(WorkShift.class)
        .join(Employee.class, Joiners.equal(WorkShift::getAssignedEmployee, Function.identity()))
        .filter((shift, employee) -> shift.getWeekDay() == DayOfWeek.TUESDAY && employee.getName().equals("Joe"))
        .reward(HardSoftScore.ONE_SOFT)
        .asConstraint("Joe prefers working on Tuesdays");
  }

  private static Constraint preferNonConsecutiveShifts(ConstraintFactory constraintFactory) {
    return constraintFactory.forEach(WorkShift.class)
        .groupBy(shift -> shift.getAssignedEmployee(), ConstraintCollectors.toList())
        .filter((employee, shifts) -> {
          List<DayOfWeek> days = shifts.stream().map(WorkShift::getWeekDay).sorted().toList();
          DayOfWeek tmp = null;
          for (DayOfWeek day : days) {
            if (tmp != null && Math.abs(day.ordinal() - tmp.ordinal()) == 1) {
              return true;
            }
            tmp = day;
          }
          return false;
        })
        .penalize(HardSoftScore.ONE_SOFT)
        .asConstraint("Avoid consecutive shifts by same employee");
  }

  public Function<ConstraintFactory, Constraint> getConstraintSpecifier() {
    return constraintSpecifier;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  @Override
  public String toString() {
    return name() + " // Enabled: " + isEnabled;
  }

}
