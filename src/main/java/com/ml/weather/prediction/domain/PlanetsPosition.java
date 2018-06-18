package com.ml.weather.prediction.domain;

public enum PlanetsPosition {
    ALIGNED("ALIGNED"),
    ALIGNED_WITH_SUN("ALIGNED_WITH_SUN"),
    NOT_ALIGNED_SUN_INSIDE("NOT_ALIGNED_SUN_INSIDE"),
    NOT_ALIGNED_SUN_OUTSIDE("NOT_ALIGNED_SUN_OUTSIDE");

    private final String value;

    PlanetsPosition(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PlanetsPosition fromValue(String v) {
        for (PlanetsPosition c: PlanetsPosition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}