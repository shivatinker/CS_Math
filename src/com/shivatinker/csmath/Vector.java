package com.shivatinker.csmath;

import java.util.ArrayList;
import java.util.Collection;

public class Vector extends ArrayList<Double> {

    public Vector(int dimension) {
        super(dimension);
    }

    public Vector(Collection<? extends Double> c) {
        super(c);
    }

    public Vector(double[] x) {
        super();
        for (double v : x) add(v);
    }

    public Vector() {
        super();
    }

    public double abs() {
        double res = 0.0;
        for (double x : this)
            res += x * x;
        return Math.sqrt(res);
    }
}
