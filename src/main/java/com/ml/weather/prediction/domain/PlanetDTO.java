package com.ml.weather.prediction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.util.Precision;

import java.awt.geom.Point2D;

@Data
@Builder
@AllArgsConstructor
public class PlanetDTO {

    private final double sunDistance;
    private final double angularVelocity;

    public double getDaysPerYear(){
        return 360 / angularVelocity;
    }

    /**
     * @link https://stackoverflow.com/questions/9871727/how-to-get-coordinates-of-a-point-in-a-coordinate-system-based-on-angle-and-dist/9871800#9871800
     */
    public Point2D getPositionInDay(double day){
        //add precision with 3 decimal
        double x = Math.sin(Math.toRadians(angularVelocity) * day) * sunDistance;
        double y = Math.cos(Math.toRadians(angularVelocity) * day) * sunDistance;
        return new Point2D.Double(x, y);
    }

    public Point2D getPositionInDayAprox(double day){
        //add precision with 3 decimal
        double x = Precision.round(Math.sin(Math.toRadians(angularVelocity) * day) * sunDistance, 2);
        double y = Precision.round(Math.cos(Math.toRadians(angularVelocity) * day) * sunDistance, 2);
        return new Point2D.Double(x, y);
    }
}
