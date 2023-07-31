package io.github.dsibilio.optademo.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.joining;

@PlanningSolution
public class TimeTable {

  @ValueRangeProvider
  @ProblemFactCollectionProperty
  private List<Employee> employees;
  @PlanningEntityCollectionProperty
  private List<WorkShift> workShifts;
  @PlanningScore
  private HardSoftScore score;

  public TimeTable(List<Employee> employees) {
    this.employees = employees;
    this.workShifts = Arrays.stream(DayOfWeek.values()).map(WorkShift::new).toList();
  }

  public TimeTable() {
    // default constructor for OptaPlanner
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  public void setEmployees(List<Employee> employees) {
    this.employees = employees;
  }

  public List<WorkShift> getWorkShifts() {
    return workShifts;
  }

  public void setWorkShifts(List<WorkShift> workShifts) {
    this.workShifts = workShifts;
  }

  public HardSoftScore getScore() {
    return score;
  }

  @Override
  public String toString() {
    int weekDayPadding = Arrays.stream(DayOfWeek.values())
        .max(Comparator.comparingInt(d -> d.name().length()))
        .map(dayOfWeek -> dayOfWeek.name().length())
        .get();

    return this.workShifts.stream().map(workShift -> ("%" + weekDayPadding + "s: %s").formatted(
            workShift.getWeekDay(),
            workShift.getAssignedEmployee() != null ? workShift.getAssignedEmployee().getName() : null)
        )
        .collect(joining("\n"));
  }
}