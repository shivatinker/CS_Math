package com.shivatinker.csmath;

import java.util.*;

import static com.shivatinker.csmath.LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL;
import static com.shivatinker.csmath.LPProblem.BoundaryConditionType.BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL;

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
                    a[i][xn + add] = -1.0;
                    c[i] = condition.c;
                    add++;
                    break;
            }
            i++;
        }
        canonicalForm = new LinearSystem(a, c);
    }

    public Result simplexOptimize() throws LinearSystem.LSException, LPException {
        return simplexOptimize(10000, null);
    }

    public Result simplexOptimize(Set<Integer> definedBasis) throws LinearSystem.LSException, LPException {
        return simplexOptimize(10000, definedBasis);
    }

    public Result simplexOptimize(int maxIterations, Set<Integer> definedBasis) throws LinearSystem.LSException, LPException {
        if (!minimize)
            return simplexOptimizeMax(maxIterations, definedBasis);
        else {
            double[] flipped = new double[functional.getDimension()];
            for (int i = 0; i < functional.getDimension(); i++)
                flipped[i] = -functional.get(i);
            LinearFunctional lf = new LinearFunctional(flipped);
            LPProblem problem = new LPProblem(lf, conditions, false);
            Result result = problem.simplexOptimizeMax(maxIterations, definedBasis);
            return new Result(result.solution, -result.value, result.infinitelyOptimizable);
        }
    }

    private Result simplexOptimizeMax(int maxIterations, Set<Integer> definedBasis) throws LPException, LinearSystem.LSException {
        System.out.println("Canonical form: ");
        System.out.println(canonicalForm);

        List<Set<Integer>> basises = new ArrayList<>();
        if (definedBasis == null) {
            List<Integer> all = new ArrayList<>();
            for (int i = 0; i < n; i++)
                all.add(i);

            basises.add(new HashSet<>(new ArrayList<>(Arrays.asList(0, 1, 4))));

            basises = Utils.getSubsets(all, n - m);
        } else
            basises.add(definedBasis);
        LinearSystem.DiagonalizationResult current = null;
        Set<Integer> basis = null;
        for (Set<Integer> b : basises)
            try {
                LinearSystem.DiagonalizationResult result1 = canonicalForm.diagonalize(b);
                System.out.printf("Chacking Basis: %s\n", b.toString());
                System.out.println(result1.system);
                boolean ok = true;
                double[] c = result1.system.getC();
                for (int i = 0; i < c.length; i++) {
                    double z = c[i];
                    Integer ii = result1.v.get(i);
                    ok &= (ii == n - 1 || z >= -1e-10);
                }
                if (ok) {
                    current = result1;
                    basis = b;
                    break;
                }
            } catch (LinearSystem.LSException e) {
                System.err.println(e.toString());
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
            int tmp2 = rows[r0];
            rows[r0] = rows[0];
            rows[0] = tmp2;
            int col = 0;
            for (int i = 0; i < n - 1; i++)
                if (a[0][i] < a[0][col])
                    col = i;
            if (a[0][col] >= 0)
                break;
            int row = 0;
            for (int i = 1; i < m; i++)
                if (a[row][col] == 0 || (a[i][col] > -1e-10 && (row == 0 || (c[i] / a[i][col] < c[row] / a[row][col]))))
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
        StringBuilder s = new StringBuilder((minimize ? "Minimize " : "Maximize ") + functional.toString() + "\n");
        for (BoundaryCondition condition : conditions)
            s.append(condition.toString()).append("\n");
        return s.toString();
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

        @Override
        public String toString() {
            ArrayList<String> vals = new ArrayList<>();
            for (int i = 0; i < a.length; i++) vals.add(String.format("%5.2f x%d", a[i], i + 1));
            String delim = " ";
            switch (type) {
                case BOUNDARY_CONDITION_TYPE_EQUAL:
                    delim = " = ";
                    break;
                case BOUNDARY_CONDITION_TYPE_LESS_OR_EQUAL:
                    delim = " <= ";
                    break;
                case BOUNDARY_CONDITION_TYPE_GREAT_OR_EQUAL:
                    delim = " >= ";
                    break;
            }
            return String.join(" + ", vals) + delim + String.format("%5.2f", c);
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
