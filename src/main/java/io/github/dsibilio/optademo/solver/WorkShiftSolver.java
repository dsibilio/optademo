package io.github.dsibilio.optademo.solver;

import io.github.dsibilio.optademo.domain.TimeTable;
import io.github.dsibilio.optademo.domain.WorkShift;
import io.github.dsibilio.optademo.repository.EmployeeInMemoryRepository;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class WorkShiftSolver {

  private final EmployeeInMemoryRepository employeeRepository;
  private final Map<Score, TimeTable> previousSolutions = new TreeMap<>();
  private TimeTable solution;

  public WorkShiftSolver(EmployeeInMemoryRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  public TimeTable solve() {
    previousSolutions.clear();
    SolverFactory<TimeTable> solverFactory = getTimeTableSolverFactory();
    Solver<TimeTable> solver = solverFactory.buildSolver();
    solver.addEventListener(bestSolutionChangedEvent ->
        previousSolutions.put(bestSolutionChangedEvent.getNewBestScore(), bestSolutionChangedEvent.getNewBestSolution())
    );
    solution = solver.solve(new TimeTable(employeeRepository.getAllEmployees()));
    return solution;
  }

  public Optional<ScoreExplanation<TimeTable, HardSoftScore>> explain() {
    SolutionManager<TimeTable, HardSoftScore> scoreManager = SolutionManager.create(getTimeTableSolverFactory());
    return Optional.ofNullable(solution)
        .map(scoreManager::explain);
  }

  private static SolverFactory<TimeTable> getTimeTableSolverFactory() {
    return SolverFactory.create(
        new SolverConfig().withSolutionClass(TimeTable.class)
            .withEntityClasses(WorkShift.class)
            .withConstraintProviderClass(WorkShiftConstraintProvider.class)
            .withTerminationSpentLimit(Duration.ofSeconds(5)));
  }

  public Map<Score, TimeTable> getPreviousSolutions() {
    return previousSolutions;
  }

}
