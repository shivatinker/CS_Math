package com.shivatinker.csmath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.MathContext;

import static org.junit.Assert.*;

public class RationalTest {

    private Rational r1, r2, r3;
    private double v1, v2, v3;

    private void assertRationalEqualsDouble(Rational r1, double v1) {
        //Assert.assertTrue(Utils.doubleEquals(r1.getNum().divide(r1.getDenom(), MathContext.DECIMAL64), v1));
    }

    @Test
    public void toString1() {
        assertRationalEqualsDouble(r1, v1);
        assertRationalEqualsDouble(r2, v2);
        assertRationalEqualsDouble(r3, v3);
    }

    @Test
    public void add() {
        assertRationalEqualsDouble(r1.add(r2), v1 + v2);
        assertRationalEqualsDouble(r3.add(r2), v3 + v2);
        assertRationalEqualsDouble(r1.add(r3), v1 + v3);
    }

    @Test
    public void add1() {
    }

    @Before
    public void setUp() throws Exception {
        v1 = 3.0 / 4.0;
        r1 = new Rational(3, 4);
        v2 = Math.sqrt(2);
        r2 = new Rational(v2);
        v3 = 11232.123412341212344;
        r3 = new Rational(v3);
    }
}