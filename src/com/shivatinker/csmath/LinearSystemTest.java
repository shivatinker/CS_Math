package com.shivatinker.csmath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class LinearSystemTest {

    private static final double EPS = 1e-6;
    private LinearSystem ls1;

    private double[][] a1 = {
            {1, 3, 2, 4},
            {5, 4, 3, 2},
            {9, 3, 4, 2},
            {6, 6, 3, 1}
    };
    private double[] c1 = {5, 4, 3, 2};

    private double[] correctSolution1 = {-25 / 6, -7 / 16, 77 / 16, -7 / 16};
    private double[] wrongSolution1 = {1, 2, 3, 4};

    private LinearSystem ls2;

    private double[][] a2 = {
            {0, 0, 2, 4},
            {5, 4, 3, 0},
            {9, 0, 4, 2},
            {6, 6, 3, 0},
            {3, 3, 1.5, 0},
            {6, 6, 3, 0}
    };
    private double[] c2 = {5, 4, 3, 2, 1, 2};

    private LinearSystem ls3;

    private double[][] a3 = {
            {1, 12, 2, 4, 6, 8},
            {5, 6, 3, 53, 3, 8},
            {9, 3, 11, 2, 1, 1},
            {6, 6, 3, 1, 10, 5}
    };
    private double[] c3 = {5, 4, 53, 2};


    @Before
    public void setUp() throws Exception {
        ls1 = new LinearSystem(a1, c1);
        ls2 = new LinearSystem(a2, c2);
        ls3 = new LinearSystem(a3, c3);
    }

    @Test
    public void getError() {
        Assert.assertTrue(ls1.getError(correctSolution1) < EPS);
        Assert.assertTrue(ls1.getError(wrongSolution1) > EPS);
    }

    @Test
    public void getVariablesCount() {
        Assert.assertEquals(4, ls1.getVariablesCount());
        Assert.assertEquals(4, ls2.getVariablesCount());
    }

    @Test
    public void getEquationsCount() {
        Assert.assertEquals(4, ls1.getEquationsCount());
        Assert.assertEquals(6, ls2.getEquationsCount());
    }

    @Test
    public void solveGauss() {
        System.out.println("Gauss 1");
        Assert.assertTrue(ls1.getError(ls1.solveGauss()) < EPS);
        System.out.println("Gauss 2");
        Assert.assertTrue(ls2.getError(ls2.solveGauss()) < EPS);
        LinearSystem ls = new LinearSystem(
                new double[][]{
                        {1},
                        {2}
                },
                new double[]{4, 3}
        );
        System.out.println("Gauss 3");
        Assert.assertNull(ls.solveGauss());
    }

    @Test
    public void toString1() {
    }

    @Test
    public void addRow() {
    }

    @Test
    public void multiplyRow() {
    }

    @Test
    public void addRow1() {
    }

    @Test
    public void diagonalize() throws LinearSystem.LSException {
        LinearSystem ls3Diagonalized = null;
        ls3Diagonalized = ls3.diagonalize(new HashSet<>(Arrays.asList(4, 1))).system;
        System.out.println(ls3Diagonalized);
        double[] c = ls3Diagonalized.getC();
        Assert.assertTrue(Utils.isZero(ls3.getError(new double[]{c[0], 0, c[1], c[2], 0, c[3]})));
    }

    @Test
    public void toString2() {
    }
}