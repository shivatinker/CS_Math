package com.shivatinker.csmath;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LinearFunctionalTest {

    @Test
    public void evaluate() {
        LinearFunctional f = new LinearFunctional(new double[]{4, 3.2, 4.1, 2});
        double expected = 4 + 3.2 * 2.3 + 4.1 * 3.2 + 2 * 4.1;
        double actual = f.evaluate(new double[]{1, 2.3, 3.2, 4.1});
        Assert.assertTrue(Math.abs(actual - expected) <= 1e-6);
    }
}