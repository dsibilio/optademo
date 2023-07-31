package io.github.dsibilio.optademo.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.DayOfWeek;
import java.util.Objects;

@PlanningEntity
public class WorkShift {

  @PlanningId
  private DayOfWeek weekDay;
  @PlanningVariable
  private Employee assignedEmployee;

  public WorkShift(DayOfWeek weekDay) {
    this.weekDay = weekDay;
  }

  public WorkShift() {
    // default constuctor for OptaPlanner
  }

  public DayOfWeek getWeekDay() {
    return weekDay;
  }

  public Employee getAssignedEmployee() {
    return assignedEmployee;
  }

  public void setAssignedEmployee(Employee assignedEmployee) {
    this.assignedEmployee = assignedEmployee;
  }

  @Override
  public String toString() {
    return "WorkShift{" +
        "weekDay=" + weekDay +
        ", assignedEmployee=" + assignedEmployee +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WorkShift workShift = (WorkShift) o;
    return weekDay == workShift.weekDay;
  }

  @Override
  public int hashCode() {
    return Objects.hash(weekDay);
  }

}
