package com.shivatinker.csmath;

import java.util.ArrayList;
import java.util.Arrays;

public class LinearFunctional implements IFunctional {

    private double[] a;

    public int getDimension() {
        return dimension;
    }

    private final int dimension;

    public double get(int index) {
        return a[index];
    }

    public LinearFunctional(int dimension) {
        this.dimension = dimension;
        a = new double[dimension];
    }

    public LinearFunctional(double[] a) {
        this.a = a;
        dimension = a.length;
    }

    @Override
    public double evaluate(double[] x) {
        if (x.length != dimension)
            throw new IllegalArgumentException("Wrong vector dimension");
        double res = 0.0;
        for (int i = 0; i < dimension; i++)
            res += x[i] * a[i];
        return res;
    }

    @Override
    public String toString() {
        ArrayList<String> elems = new ArrayList<>();
        for (int i = 0; i < a.length; i++)
            elems.add(String.format("%.2f x%d", a[i], i));
        return String.join(" + ", elems);
    }
}
