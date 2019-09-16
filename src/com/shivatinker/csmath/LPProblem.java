package com.shivatinker.csmath;

import java.util.*;

import static com.shivatinker.csmath.LPProblem.BoundaryConditionType.*;

public class LPProblem {

    public enum BoundaryConditionType {
        BOUNDARY_CONDITION_TYPE_EQUAL,
        BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL,
        BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL
    }

    private final int n;

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    private final int m;

    private final boolean minimize;

    public LinearFunctional getFunctional() {
        return functional;
    }

    private final LinearFunctional functional;
    private final BoundaryCondition[] conditions;

    public LinearSystem getCanonicalForm() {
        return canonicalForm;
    }

    private final LinearSystem canonicalForm;

    public LPProblem(LinearFunctional functional, BoundaryCondition[] conditions, boolean minimize) {
        this.minimize = minimize;
        this.functional = functional;
        this.conditions = conditions;

        int m = 1 + conditions.length;
        int xn = functional.getDimension();
        int n = xn + 1;
        for (BoundaryCondition condition : conditions)
            if (condition.type == BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL || condition.type == BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL)
                n++;

        this.n = n;
        this.m = m;

        double[][] a = new double[m][n];
        double[] c = new double[m];

        for (int i = 0; i < n; i++)
            a[0][i] = i < xn ? -functional.get(i) : 0;
        a[0][n - 1] = 1.0;
        c[0] = 0.0;

        int i = 1, add = 0;
        for (BoundaryCondition condition : conditions) {
            switch (condition.type) {
                case BOUNDARY_CONDITION_TYPE_EQUAL:
                    System.arraycopy(condition.a, 0, a[i], 0, condition.a.length);
                    c[i] = condition.c;
                    break;
                case BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL:
                    System.arraycopy(condition.a, 0, a[i], 0, condition.a.length);
                    a[i][xn + add] = 1.0;
                    c[i] = condition.c;
                    add++;
                    break;
                case BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL:
                    System.arraycopy(condition.a, 0, a[i], 0, condition.a.length);
                    a[i][xn + add] = 1.0;
                    c[i] = condition.c;
                    break;
            }
            i++;
        }
        canonicalForm = new LinearSystem(a, c);
    }

    public Result simplexOptimize() throws LinearSystem.LSException, LPException {
        return simplexOptimize(10000);
    }

    public Result simplexOptimize(int maxIterations) throws LinearSystem.LSException, LPException {
        if (!minimize)
            return simplexOptimizeMax(maxIterations);
        else {
            double[] flipped = new double[functional.getDimension()];
            for (int i = 0; i < functional.getDimension(); i++)
                flipped[i] = -functional.get(i);
            LinearFunctional lf = new LinearFunctional(flipped);
            LPProblem problem = new LPProblem(lf, conditions, false);
            Result result = problem.simplexOptimizeMax(maxIterations);
            return new Result(result.solution, -result.value, result.infinitelyOptimizable);
        }
    }

    private Result simplexOptimizeMax() throws LPException, LinearSystem.LSException {
        return simplexOptimizeMax(10000);
    }

    private Result simplexOptimizeMax(int maxIterations) throws LPException, LinearSystem.LSException {
        Result result;
        System.out.println("Canonical form: ");
        System.out.println(canonicalForm);
        List<Set<Integer>> basises = Collections.singletonList(new HashSet<>(Arrays.asList(0, 1)));// Utils.getSubsets(all, n - m);
        LinearSystem.DiagonalizationResult current = null;
        Set<Integer> basis = null;
        for (Set<Integer> b : basises)
            try {
                current = canonicalForm.diagonalize(b);
                basis = b;
            } catch (LinearSystem.LSException ignored) {
            }
        if (current == null)
            throw new LPException("Cannot find initial basis");
        System.out.println(String.format("Initial basis: %s", basis));
        System.out.println(current.system);
        int[] rows = new int[m];
        for (int i = 0; i < m; i++)
            rows[i] = current.v.get(i);
        for (int iter = 0; iter < maxIterations; iter++) {
            double[][] a = current.system.getA();
            double[] c = current.system.getC();
            int r0 = 0;
            for (int i = 0; i < m; i++)
                if (a[i][n - 1] == 1)
                    r0 = i;
            double[] tmp = a[r0];
            a[r0] = a[0];
            a[0] = tmp;
            double tmp1 = c[r0];
            c[r0] = c[0];
            c[0] = tmp1;
            int col = 0;
            for (int i = 0; i < n - 1; i++)
                if (a[0][i] < a[0][col])
                    col = i;
            if (a[0][col] >= 0)
                break;
            int row = 0;
            for (int i = 1; i < m; i++)
                if (a[row][col] == 0 || (a[i][col] > 0 && (row == 0 || (c[i] / a[i][col] < c[row] / a[row][col]))))
                    row = i;
            if (row == 0)
                return new Result(null, Double.MAX_VALUE, true);

            basis.add(rows[row]);
            basis.remove(col);
            current = current.system.diagonalize(basis);
            for (int i = 0; i < m; i++)
                rows[i] = current.v.get(i);

            System.out.println(String.format("Current basis: %s, Rows : %s", basis, Arrays.toString(rows)));
            System.out.println(current.system);
        }

        double[] resSolution = new double[functional.getDimension()];
        for (int i = 0; i < m; i++)
            if (current.v.get(i) < functional.getDimension())
                resSolution[current.v.get(i)] = current.system.getC()[i];

        return new Result(resSolution, functional.evaluate(resSolution), false);
    }

    @Override
    public String toString() {
        return (minimize ? "Minimize " : "Maximize ") + functional.toString();
    }

    public static class BoundaryCondition {
        public final double[] a;
        public final double c;
        public final BoundaryConditionType type;

        public BoundaryCondition(double[] a, double c, BoundaryConditionType type) {
            this.a = a;
            this.c = c;
            this.type = type;
        }
    }

    public class LPException extends Throwable {
        public LPException(String s) {
            super(s);
        }
    }

    public class Result {
        final double[] solution;
        final double value;
        final boolean infinitelyOptimizable;

        public Result(double[] solution, double value, boolean infinitelyOptimizable) {
            this.solution = solution;
            this.value = value;
            this.infinitelyOptimizable = infinitelyOptimizable;
        }
    }
}
