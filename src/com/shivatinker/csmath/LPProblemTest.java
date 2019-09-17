package com.shivatinker.csmath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class LPProblemTest {
    private ArrayList<LPProblem> problems;
    private ArrayList<double[]> solves = new ArrayList<>();

    @Test
    public void simplexOptimize() throws LPProblem.LPException, LinearSystem.LSException {
        for (int i = 0; i < problems.size(); i++) {
            LPProblem problem = problems.get(i);
            System.out.printf("\nRunning test %d...\n%s\n", i + 1, problem.toString());
            LPProblem.Result result = problem.simplexOptimize();
            System.out.println(String.format("X: %s Z = %.3f", Arrays.toString(result.solution), result.value));
            Assert.assertTrue(Utils.doubleEquals(result.value, problem.getFunctional().evaluate(result.solution)));
            for (int j = 0; j < result.solution.length; j++)
                Assert.assertTrue(Utils.doubleEquals(result.solution[j], solves.get(i)[j]));
        }
    }

    @Test
    public void solve() throws LPProblem.LPException, LinearSystem.LSException {
        ArrayList<LPProblem.BoundaryCondition> conditions = new ArrayList<>() {{
            add(new LPProblem.BoundaryCondition(new double[]{5, 1, 1, 0}, 4, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_EQUAL));
            add(new LPProblem.BoundaryCondition(new double[]{2, 1, 0, 1}, 3, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL));
            add(new LPProblem.BoundaryCondition(new double[]{1, 1, 0, 0}, 3, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
        }};
        LinearFunctional functional = new LinearFunctional(new double[]{1, 1, 100, 100});
        LPProblem toSolve = new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), true);
        LPProblem.Result result = toSolve.simplexOptimize(new HashSet<>(Arrays.asList(0, 1, 4)));
        System.out.println(String.format("X: %s Z = %.3f", Arrays.toString(result.solution), result.value));
    }

    @Before
    public void setUp() {
        problems = new ArrayList<>() {{
            ArrayList<LPProblem.BoundaryCondition> conditions = new ArrayList<>() {{
                add(new LPProblem.BoundaryCondition(new double[]{1, 2}, 6, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{2, 1}, 8, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{-1, 1}, 1, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{0, 2}, 2, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            }};

            LinearFunctional functional = new LinearFunctional(new double[]{3, 2});
            add(new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), false));
            solves.add(new double[]{3.5, 1});

            conditions = new ArrayList<>() {{
                add(new LPProblem.BoundaryCondition(new double[]{1, 1}, 4, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{5, 2}, 10, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            }};
            functional = new LinearFunctional(new double[]{5, 3});
            add(new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), false));
            solves.add(new double[]{2 / 3.0, 3 + 1 / 3.0});
            add(new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), true));
            solves.add(new double[]{0, 0});

            conditions = new ArrayList<>() {{
                add(new LPProblem.BoundaryCondition(new double[]{5, 1}, 7, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{2, 1}, 6, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{1, 1}, 6, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            }};
            functional = new LinearFunctional(new double[]{1, 1});
            add(new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), true));
            solves.add(new double[]{1 / 3.0, 5 + 1 / 3.0});

            conditions = new ArrayList<>() {{
                add(new LPProblem.BoundaryCondition(new double[]{5, 1}, 3, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{2, 1}, 2, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL));
                add(new LPProblem.BoundaryCondition(new double[]{1, 1}, 2, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            }};
            functional = new LinearFunctional(new double[]{1, 1});
            add(new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), true));
            solves.add(new double[]{1 / 3.0, 1 + 1 / 3.0});
        }};
    }

    @Test
    public void getCanonicalForm() {
        for (LPProblem problem : problems) {
            System.out.println(String.format("N = %d M = %d", problem.getN(), problem.getM()));
            System.out.println(problem.getCanonicalForm());
        }
    }
}