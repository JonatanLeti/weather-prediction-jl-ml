package com.ml.weather.prediction.services;

import org.apache.commons.math3.util.Precision;

import java.awt.geom.Point2D;

import java.lang.Math;

public class GalaxyWeatherHelper {

    public double calculateDistance(Point2D a, Point2D b) {
        return Math.sqrt(
                Math.pow((a.getX() - b.getX()), 2) +
                Math.pow((a.getY() - b.getY()), 2)
        );
    }

    /**
     * @link https://stackoverflow.com/questions/2145571/java-calculating-area-of-a-triangle/2145633#2145633
     * @link https://en.wikipedia.org/wiki/Triangle#Using_Heron.27s_formula
     */
    public double calculateTriangleArea(Point2D a, Point2D b, Point2D c) {
        double ab = this.calculateDistance(a, b);
        double bc = this.calculateDistance(c, b);
        double ca = this.calculateDistance(a, c);

        double shape = (ab + bc + ca) / 2;
        return Math.sqrt(shape * (shape - ab) * (shape - bc) * (shape - ca));
    }

    @Deprecated
    public boolean pointsAreAlineated(Point2D a, Point2D b, Point2D c) {
        return calculateTriangleArea(a, b, c) < 1;
    }

    @Deprecated
    public boolean pointAreAlineatedSimply(Point2D a, Point2D b, Point2D c){
        return calculateSimpleTriangleArea(a, b, c) < 1;
    }

    public double calculateSimpleTriangleArea(Point2D a, Point2D b, Point2D c) {
        return
                Math.abs(
                        Precision.round(
                                (
                        a.getX() * (b.getY() - c.getY()) +
                        b.getX() * (c.getY() - a.getY()) +
                        c.getX() * (a.getY() - b.getY())
                    ) / 2, 2)
        );
    }



    /**
     * @link http://www.crazyforcode.com/check-point-lies-triangle/
     */
    public boolean pointIsInsideTriangle(Point2D a, Point2D b, Point2D c, Point2D p) {

        double ABC = calculateSimpleTriangleArea(a, b, c);
        double PBC = calculateSimpleTriangleArea(p, b, c);
        double APC = calculateSimpleTriangleArea(a, p, c);
        double ABP = calculateSimpleTriangleArea(a, b, p);

        return (ABC == PBC + APC + ABP);
    }

    /**
     * @link https://www.geeksforgeeks.org/program-find-slope-line/
     */
    public boolean isAlignedByDistanceAndAngleVel(double d1, double d2, double d3, double av1, double av2, double av3, double day){

        // si la pendiente entre tres puntos es 0, estan alineados?
        double pendiente =
                d1 * d2 * Math.sin((av2 - av1) * Precision.round(day, 2)) +
                d2 * d3 * Math.sin((av3 - av2) * Precision.round(day, 2)) +
                d3 * d1 * Math.sin((av1 - av3) * Precision.round(day, 2));

        return pendiente < 1;
    }
}