package com.shivatinker.csmath;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class LPProblemTest {
    private LPProblem problem1;
    private LPProblem problem2;

    @Test
    public void simplexOptimize() throws LPProblem.LPException, LinearSystem.LSException {
        LPProblem.Result result1 = problem1.simplexOptimize();
        System.out.println(String.format("X: %s Z = %.3f", Arrays.toString(result1.solution), result1.value));
        LPProblem.Result result2 = problem2.simplexOptimize();
        System.out.println(String.format("X: %s Z = %.3f", Arrays.toString(result2.solution), result2.value));
    }

    @Before
    public void setUp() throws Exception {
        ArrayList<LPProblem.BoundaryCondition> conditions = new ArrayList<>() {{
            add(new LPProblem.BoundaryCondition(new double[]{1, 2}, 6, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            add(new LPProblem.BoundaryCondition(new double[]{2, 1}, 8, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            add(new LPProblem.BoundaryCondition(new double[]{-1, 1}, 1, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            add(new LPProblem.BoundaryCondition(new double[]{0, 2}, 2, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
        }};
        LinearFunctional functional = new LinearFunctional(new Vector(new double[]{3, 2}));
        problem1 = new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), false);

        conditions = new ArrayList<>() {{
            add(new LPProblem.BoundaryCondition(new double[]{1, 1}, 4, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
            add(new LPProblem.BoundaryCondition(new double[]{5, 2}, 10, LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL));
        }};
        functional = new LinearFunctional(new Vector(new double[]{5, 3}));
        problem2 = new LPProblem(functional, conditions.toArray(new LPProblem.BoundaryCondition[0]), false);
    }

    @Test
    public void getCanonicalForm() {
        System.out.println(String.format("N = %d M = %d", problem1.getN(), problem1.getM()));
        System.out.println(problem1.getCanonicalForm());
    }
}