package com.shivatinker.csmath;

import java.math.BigDecimal;

public class Rational {

    public static final double RATIONAL_ACCURACY = 6;
    private static final Rational ZERO = new Rational(0, 1);

    public Rational(BigDecimal num, BigDecimal denom) {
        if (num.equals(BigDecimal.ZERO))
            denom = BigDecimal.ONE;
        else {
            BigDecimal gcd = gcd(num, denom);
            if (!gcd.equals(BigDecimal.ONE)) {
                num = num.divide(gcd);
                denom = num.divide(gcd);
            }
        }
        this.num = num;
        this.denom = denom;
    }

    private BigDecimal gcd(BigDecimal a, BigDecimal b) {
        return b.equals(BigDecimal.ZERO) ? a : gcd(b, a.remainder(b));
    }

    public BigDecimal getNum() {
        return num;
    }

    public BigDecimal getDenom() {
        return denom;
    }

    private final BigDecimal num;
    private final BigDecimal denom;

    public Rational(long num, long denom) {
        this(BigDecimal.valueOf(num), BigDecimal.valueOf(denom));
    }

    public Rational(double v) {
        String s = String.valueOf(v);
        long digitsDec = s.length() - 1 - s.indexOf('.');

        long denom = 1;
        for (int i = 0; i < digitsDec; i++) {
            v *= 10;
            denom *= 10;
        }
        long num = Math.round(v);

        Rational r = new Rational(num, denom);

        this.num = r.num;
        this.denom = r.denom;
    }

    public Rational add(Rational r) {
        return new Rational(num.multiply(r.denom).add(denom.multiply(r.num)), r.denom.multiply(denom));
    }

    public Rational add(double r) {
        return add(new Rational(r));
    }

    @Override
    public String toString() {
        return num + "/" + denom;
    }
}
