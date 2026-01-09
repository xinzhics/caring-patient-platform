package com.caring.sass.ai.utils;

public class Rational {
    static double parse(String s) {
        if (s == null || s.isEmpty() || s.equals("N/A") || s.equals("0/0")) return 0.0;
        try {
            String[] parts = s.split("/");
            if (parts.length == 2) {
                int num = Integer.parseInt(parts[0]);
                int den = Integer.parseInt(parts[1]);
                if (den == 0) return 0.0;
                return (double) num / den;
            } else {
                return Double.parseDouble(s);
            }
        } catch (Exception e) {
            return 0.0;
        }
    }
}