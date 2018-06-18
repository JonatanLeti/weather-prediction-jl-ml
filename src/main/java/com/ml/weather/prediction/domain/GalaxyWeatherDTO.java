package com.ml.weather.prediction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.util.Precision;

import java.awt.geom.Point2D;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class GalaxyWeatherDTO {

    private BigDecimal id;
    private int day;
    private double xPositionFerengi;
    private double yPositionFerengi;
    private double xPositionVulcano;
    private double yPositionVulcano;
    private double xPositionBetasoide;
    private double yPositionBetasoide;

    private double planetsTriangleArea;
    private WeatherStatus weatherStatus;
    private PlanetsPosition planetsPosition;
}
