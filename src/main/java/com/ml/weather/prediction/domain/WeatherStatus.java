package com.ml.weather.prediction.domain;

public enum WeatherStatus {
    RAIN("RAIN"), HEAVY_RAIN("HEAVY_RAIN"), DROUGHT("DROUGHT"), OPTIMUM("OPTIMUM"), UNKNOWN("UNKNOWN");

    private final String value;

    WeatherStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeatherStatus fromValue(String v) {
        for (WeatherStatus c: WeatherStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}