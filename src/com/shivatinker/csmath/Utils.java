package com.shivatinker.csmath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Utils {
    private Utils() {
    }

    public static final double EQUALITY_EPS = 1e-8;

    public static boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < EQUALITY_EPS;
    }

    public static boolean isZero(double a) {
        return doubleEquals(a, 0.0);
    }

    private static void getSubsets(List<Integer> superSet, int k, int idx, Set<Integer> current, List<Set<Integer>> solution) {
        //successful stop clause
        if (current.size() == k) {
            solution.add(new HashSet<>(current));
            return;
        }
        //unsuccessful stop clause
        if (idx == superSet.size()) return;
        Integer x = superSet.get(idx);
        current.add(x);
        //"guess" x is in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
    }

    public static List<Set<Integer>> getSubsets(List<Integer> superSet, int k) {
        List<Set<Integer>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<>(), res);
        return res;
    }
}
