package com.shivatinker.csmath;

import java.util.ArrayList;
import java.util.Arrays;

public class LinearFunctional implements IFunctional {

    private Vector a;

    public int getDimension() {
        return dimension;
    }

    private final int dimension;

    public double get(int index) {
        return a.get(index);
    }

    public double set(int index, double element) {
        return a.set(index, element);
    }

    public LinearFunctional(int dimension) {
        this.dimension = dimension;
        a = new Vector(dimension);
    }

    public LinearFunctional(Vector a) {
        this.a = a;
        dimension = a.size();
    }

    @Override
    public double evaluate(Vector x) {
        if (x.size() != dimension)
            throw new IllegalArgumentException("Wrong vector dimension");
        double res = 0.0;
        for (int i = 0; i < dimension; i++)
            res += x.get(i) * a.get(i);
        return res;
    }
}
