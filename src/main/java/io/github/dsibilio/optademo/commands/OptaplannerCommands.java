package io.github.dsibilio.optademo.commands;

import io.github.dsibilio.optademo.domain.Employee;
import io.github.dsibilio.optademo.domain.TimeTable;
import io.github.dsibilio.optademo.repository.EmployeeInMemoryRepository;
import io.github.dsibilio.optademo.solver.WorkShiftConstraint;
import io.github.dsibilio.optademo.solver.WorkShiftSolver;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ShellComponent
public class OptaplannerCommands {

  private final EmployeeInMemoryRepository employeeRepository;
  private final WorkShiftSolver workShiftSolver;
  private Employee selectedEmployee = null;

  public OptaplannerCommands(EmployeeInMemoryRepository employeeRepository, WorkShiftSolver workShiftSolver) {
    this.employeeRepository = employeeRepository;
    this.workShiftSolver = workShiftSolver;
  }

  @ShellComponent
  private class EmployeeCommands {
    @ShellMethod(key = "add", value = "Add an employee.")
    public String addEmployee(Employee employee) {
      employeeRepository.addEmployee(employee);
      return "Added: " + employee;
    }

    @ShellMethod(key = "get", value = "Get an employee by name.")
    public String getByName(String name) {
      return "Found: " + employeeRepository.getEmployeeByName(name);
    }

    @ShellMethod(key = "remove", value = "Remove an employee by name.")
    public String removeByName(String name) {
      return "Removed: " + employeeRepository.deleteEmployeeByName(name);
    }

    @ShellMethod(key = "list", value = "List all employees.")
    public String listAll() {
      return "Employees: " + employeeRepository.getAllEmployees();
    }

    @ShellMethod(key = "select", value = "Select an employee by name.")
    public String select(String name) {
      return Optional.ofNullable(employeeRepository.getEmployeeByName(name))
          .map(e -> selectedEmployee = e)
          .map(e -> "Selected: " + selectedEmployee)
          .orElseThrow(() -> new RuntimeException("Employee not found: " + name));
    }

    @ShellMethod(key = "current", value = "Show the currently selected employee.")
    public String current() {
      return "Currently selected: " + selectedEmployee;
    }
  }

  @ShellComponent
  private class WorkShiftCommands {
    @ShellMethod(key = "constraints", value = "Show all work shift constraints")
    public String constraints() {
      return "Work shift constraints: " + Arrays.toString(WorkShiftConstraint.values());
    }

    @ShellMethod(key = "toggle-constraint", value = "Toggle constraint on/off by name")
    public String toggleConstraint(String name) {
      String result;
      try {
        WorkShiftConstraint workShiftConstraint = WorkShiftConstraint.valueOf(name);
        workShiftConstraint.setEnabled(!workShiftConstraint.isEnabled());
        result = "Toggled " + workShiftConstraint;
      } catch (IllegalArgumentException e) {
        result = "No constraint found for key: " + name;
      }
      return result;
    }

    @ShellMethod(key = "plan", value = "Plan the next work week according to current roster.")
    public TimeTable plan() {
      return workShiftSolver.solve();
    }

    @ShellMethod(key = "explain", value = "Explain the current planning.")
    public String explain() {
      return workShiftSolver.explain()
          .map(Object::toString)
          .orElse("No solution to explain");
    }

    @ShellMethod(key = "alternatives", value = "Show the history of attempted solutions.")
    public String alternatives() {
      String alternatives = Stream.of(workShiftSolver.getPreviousSolutions())
          .map(Map::entrySet)
          .flatMap(Collection::stream)
          .map(entry -> "Score: %s%nSolution:%n%s".formatted(entry.getKey(), entry.getValue()))
          .collect(Collectors.joining("\n-------------------\n"));

      return alternatives.isEmpty() ? "No alternative solutions were attempted." : alternatives;
    }
  }

}
