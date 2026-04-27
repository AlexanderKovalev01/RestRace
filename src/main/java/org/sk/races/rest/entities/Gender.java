package org.sk.races.rest.entities;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender fromString(String s) {
        if (s == null) return null;

        s = s.trim().toUpperCase();

        if (s.equals("M") || s.equals("MALE")) {
            return MALE;
        } else if (s.equals("F") || s.equals("FEMALE")) {
            return FEMALE;
        } else {
            return null;
        }
    }
}