package com.shivatinker.csmath;

import java.util.*;

import static com.shivatinker.csmath.Utils.*;

public class LinearSystem {
    private final int n;
    private final int m;

    public double[][] getA() {
        double[][] res = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            System.arraycopy(a[i], 0, res[i], 0, a[0].length);
        return res;
    }

    public double[] getC() {
        return Arrays.copyOf(c, c.length);
    }

    private final double[][] a;
    private final double[] c;

    public LinearSystem(double[][] a, double[] c) {
        this.a = a;
        this.c = c;
        if (a.length != c.length)
            throw new IllegalArgumentException("Wrong vector dimension");
        if (a.length > 0)
            for (double[] ai : a)
                if (ai.length != a[0].length)
                    throw new IllegalArgumentException("Wrong vector dimension");
        m = c.length;
        if (a.length > 0)
            n = a[0].length;
        else
            n = 0;
    }

    public double getError(double[] x) {
        if (x.length != n)
            throw new IllegalArgumentException("Wrong vector dimension");
        double res = 0.0;
        for (int i = 0; i < a.length; i++) {
            double r = 0.0;
            for (int j = 0; j < a[i].length; j++) r += a[i][j] * x[j];
            res += r - c[i];
        }
        return res;
    }

    public LinearSystem addRow(int from, int to) {
        return addRow(from, to, 1.0);
    }

    public LinearSystem multiplyRow(int r, double v) {
        double[][] a = this.a;
        double[] c = this.c;
        for (int i = 0; i < n; i++)
            a[r][i] *= v;
        c[r] *= v;
        return new LinearSystem(a, c);
    }

    public LinearSystem addRow(int from, int to, double v) {
        double[][] a = this.a;
        double[] c = this.c;
        for (int i = 0; i < n; i++)
            a[to][i] += a[from][i] * v;
        c[to] += c[from] * v;
        return new LinearSystem(a, c);
    }

    public DiagonalizationResult diagonalize(Set<Integer> basis) throws LSException {
        DiagonalizationResult res = new DiagonalizationResult();
        if (n < m)
            throw new IllegalStateException("Linear system n must be greater than m");
        if (basis.size() != n - m)
            throw new IllegalArgumentException("Basis size must be equal to n - m");
        for (Integer x : basis)
            if (x < 0 || x >= n)
                throw new IllegalArgumentException(String.format("Basis contains illegal variable number: x%d", x));
        LinearSystem system = this;
        HashSet<Integer> done = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (basis.contains(i))
                continue;
            int r = -1;
            for (int j = 0; j < m; j++)
                if (!Utils.isZero(a[j][i]) && !done.contains(j)) {
                    r = j;
                    break;
                }
            if (r == -1)
                throw new LSException(String.format("Cannot diagonalize variable x%d, because all its coefficients equal zero", i));
            //continue;
            system = system.multiplyRow(r, 1.0 / a[r][i]);
            for (int j = 0; j < m; j++)
                if (r != j)
                    system = system.addRow(r, j, -system.getA()[j][i]);
            done.add(r);
            res.v.put(r, i);
//            System.out.println(system);
        }
        res.system = system;
        return res;
    }

    public class DiagonalizationResult {
        public LinearSystem system;
        public Map<Integer, Integer> v = new HashMap<>();
    }

    public double[] solveGauss() {
        double[][] a = this.a;
        double[] c = this.c;
        System.out.println(new LinearSystem(a, c).toString());
        for (int k = 0; k < Math.min(m - 1, n); k++) {
            int z = k;
            while (z < m && a[z][k] == 0) z++;
            if (z == m) {
                continue;
            }
            double[] tmp = a[k];
            a[k] = a[z];
            a[z] = tmp;
            for (int i = k + 1; i < m; i++) {
                double coef = -a[i][k] / a[k][k];
                for (int j = k; j < n; j++)
                    a[i][j] += coef * a[k][j];
                c[i] += coef * c[k];
            }
        }
        System.out.println(new LinearSystem(a, c).toString());
        double[] sol = new double[n];
        boolean[] done = new boolean[n];
        for (int i = m - 1; i >= 0; i--) {
            double leftSum = 0.0;
            ArrayList<Integer> todo = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (!isZero(a[i][j]) && !done[j])
                    todo.add(j);
                if (done[j])
                    leftSum += sol[j] * a[i][j];
            }
            if (todo.size() == 0)
                if (doubleEquals(leftSum, c[i]))
                    continue;
                else
                    return null;

            for (int j : todo) {
                sol[j] = 0.0;
                done[j] = true;
            }
            sol[todo.get(0)] = (c[i] - leftSum) / a[i][todo.get(0)];
        }
        //System.out.println(Arrays.toString(sol));
        return sol;
    }

    @Override
    public String toString() {
        String[] ss = new String[m];
        for (int i = 0; i < ss.length; i++) {
            String[] vars = new String[n];
            for (int j = 0; j < n; j++)
                vars[j] = String.format("%6.2f x%d", a[i][j], j);
            ss[i] = String.join("  ", vars);
            ss[i] += " = " + String.format("%6.2f", c[i]);
        }
        return "{ " + String.join("\n  ", ss) + "  }";
    }

    public int getVariablesCount() {
        return n;
    }

    public int getEquationsCount() {
        return m;
    }

    public class LSException extends Throwable {
        public LSException(String s) {
            super(s);
        }
    }
}
