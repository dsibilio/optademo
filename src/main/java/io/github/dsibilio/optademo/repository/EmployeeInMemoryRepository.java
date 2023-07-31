package io.github.dsibilio.optademo.repository;

import io.github.dsibilio.optademo.domain.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeInMemoryRepository {

  private final Map<String, Employee> employeeMap = new HashMap<>();

  public Employee addEmployee(Employee employee) {
    return employeeMap.putIfAbsent(employee.getName(), employee);
  }

  public Employee getEmployeeByName(String name) {
    return employeeMap.get(name);
  }

  public List<Employee> getAllEmployees() {
    return new ArrayList<>(employeeMap.values());
  }

  public Employee deleteEmployeeByName(String name) {
    return employeeMap.remove(name);
  }
}
