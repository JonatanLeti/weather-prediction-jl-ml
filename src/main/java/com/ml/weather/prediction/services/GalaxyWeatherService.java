package com.ml.weather.prediction.services;

import com.ml.weather.prediction.db.GalaxyWeatherDAO;
import com.ml.weather.prediction.domain.GalaxyWeatherDTO;
import com.ml.weather.prediction.domain.PlanetDTO;
import com.ml.weather.prediction.domain.PlanetsPosition;
import com.ml.weather.prediction.domain.WeatherStatus;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.awt.geom.Point2D;
import java.rmi.UnexpectedException;
import java.util.List;

@Singleton
@Slf4j
public class GalaxyWeatherService {

    private final GalaxyWeatherHelper galaxyWeatherHelper;
    private final GalaxyWeatherDAO galaxyWeatherDAO;
    private final PlanetDTO FERENGI;
    private final PlanetDTO VULCANO;
    private final PlanetDTO BETASOIDE;
    private final Point2D SUN_POSITION = new Point2D.Double(0, 0);


    @Inject
    public GalaxyWeatherService(GalaxyWeatherHelper galaxyWeatherHelper,
                                GalaxyWeatherDAO galaxyWeatherDAO,
                                @Named("planet.ferengi.sun.distance") long ferengiSunDistance,
                                @Named("planet.ferengi.angular.velocity") int ferengiAngularVelocity,
                                @Named("planet.vulcano.sun.distance") long vulcanoSunDistance,
                                @Named("planet.vulcano.angular.velocity") int vulcanoAngularVelocity,
                                @Named("planet.betasoide.sun.distance") long betasoideSunDistance,
                                @Named("planet.betasoide.angular.velocity") int betasoideAngularVelocity
    ) {
        this.galaxyWeatherHelper = galaxyWeatherHelper;
        this.galaxyWeatherDAO = galaxyWeatherDAO;

        this.FERENGI = new PlanetDTO(ferengiSunDistance, ferengiAngularVelocity);
        this.VULCANO = new PlanetDTO(vulcanoSunDistance, vulcanoAngularVelocity);
        this.BETASOIDE = new PlanetDTO(betasoideSunDistance, betasoideAngularVelocity);

    }

    public GalaxyWeatherDTO calculateWeatherInDay(Integer day) {

        if (day == null || day < 0) {
            throw new IllegalArgumentException("PARAMETER 'day' IS REQUIRED");
        }

        Point2D ferengiPosition = FERENGI.getPositionInDayAprox(day);
        Point2D vulcanoPosition = VULCANO.getPositionInDayAprox(day);
        Point2D betasoide = BETASOIDE.getPositionInDayAprox(day);

        if (ferengiPosition == null || vulcanoPosition == null || betasoide == null) {
            throw new IllegalArgumentException("An error ocurred trying to get planet's positions");
        }

        //boolean planetsAreAligned = galaxyWeatherHelper.pointsAreAlineated(ferengiPosition, vulcanoPosition, betasoide);

        boolean planetsAreAligned = galaxyWeatherHelper.isAlignedByDistanceAndAngleVel(
                FERENGI.getSunDistance(),
                VULCANO.getSunDistance(),
                BETASOIDE.getSunDistance(),
                FERENGI.getAngularVelocity(),
                VULCANO.getAngularVelocity(),
                BETASOIDE.getAngularVelocity(),day);

        //boolean twoPlanetsAreAlignedWithSun = galaxyWeatherHelper.pointsAreAlineated(SUN_POSITION, ferengiPosition, vulcanoPosition);
        boolean twoPlanetsAreAlignedWithSun = galaxyWeatherHelper.isAlignedByDistanceAndAngleVel(
                0,
                VULCANO.getSunDistance(),
                BETASOIDE.getSunDistance(),
                0,
                VULCANO.getAngularVelocity(),
                BETASOIDE.getAngularVelocity(),day);

        boolean sunIsInsidePlanetsArea = galaxyWeatherHelper.pointIsInsideTriangle(ferengiPosition, vulcanoPosition, betasoide, SUN_POSITION);
        double triangleArea = galaxyWeatherHelper.calculateSimpleTriangleArea(ferengiPosition, vulcanoPosition, betasoide);

        Tuple2<PlanetsPosition, WeatherStatus> t = buildPlanetPositionAndWeather(planetsAreAligned, twoPlanetsAreAlignedWithSun, sunIsInsidePlanetsArea);

        return GalaxyWeatherDTO.builder()
                .day(day)
                .xPositionFerengi(ferengiPosition.getX())
                .yPositionFerengi(ferengiPosition.getY())
                .xPositionVulcano(vulcanoPosition.getX())
                .yPositionVulcano(vulcanoPosition.getY())
                .xPositionBetasoide(betasoide.getX())
                .yPositionBetasoide(betasoide.getY())
                .planetsTriangleArea(triangleArea)
                .planetsPosition(t.v1())
                .weatherStatus(t.v2())
                .build();
    }

    private Tuple2<PlanetsPosition, WeatherStatus> buildPlanetPositionAndWeather(boolean planetsAreAligned, boolean twoPlanetsAreAlignedWithSun, boolean sunIsInsidePlanetsArea) {

        if (planetsAreAligned) {
            if (twoPlanetsAreAlignedWithSun) {
                return Tuple.tuple(
                        PlanetsPosition.ALIGNED_WITH_SUN,
                        WeatherStatus.DROUGHT);
            } else {
                return Tuple.tuple(
                        PlanetsPosition.ALIGNED,
                        WeatherStatus.OPTIMUM);
            }

        } else {
            if (sunIsInsidePlanetsArea) {
                return Tuple.tuple(
                        PlanetsPosition.NOT_ALIGNED_SUN_INSIDE,
                        WeatherStatus.RAIN);
            } else {
                return Tuple.tuple(
                        PlanetsPosition.NOT_ALIGNED_SUN_OUTSIDE,
                        WeatherStatus.UNKNOWN);
            }
        }
    }

    public boolean saveNewGalaxyWeatherDTO(GalaxyWeatherDTO dto) throws Exception{
        log.info("Trying to sa new weather {}", dto);
        try {
            return galaxyWeatherDAO.saveNewGalaxyWeatherDTO(dto);
        }catch (Exception e){
            log.error("An error ocurred trying to save new weather {}", dto, e);
            throw new UnexpectedException("An error ocurred trying to save new weather " + dto.toString(), e);
        }
    }

    public GalaxyWeatherDTO findWeatherByDay(int day) throws Exception {
        log.info("Trying to find weather by day or throw");
        GalaxyWeatherDTO dto;

        try {
            dto = galaxyWeatherDAO.findWeatherByDay(day);

            if(dto == null){
                log.warn("Weather not found for day {}", day);
            }
        } catch (Exception e) {
            log.error("An error ocurred trying to find weather by day {}", day, e);
            throw new UnexpectedException("Weather not found for day " + day + ". Try with &forceRecalculate=true");
        }
        return dto;
    }

    public List<GalaxyWeatherDTO> findWeatherPeriod(WeatherStatus weatherStatus) throws Exception{
        log.info("Trying to obtain {} periods", weatherStatus);
        List<GalaxyWeatherDTO> weatherDTOList;

        try {
            weatherDTOList = galaxyWeatherDAO.findWeatherPeriod(weatherStatus);
            if(CollectionUtils.isEmpty(weatherDTOList)){
                log.warn("Weather period not found for {}", weatherStatus);
            }
        } catch (Exception e) {
            log.error("An error ocurred trying to find weather period {}", weatherStatus, e);
            throw new UnexpectedException("Weather period " + weatherStatus + "not found or fail.", e);
        }

        return weatherDTOList;
    }

    public List<Tuple2<WeatherStatus, Integer>> findTotalDayByWeather() throws  Exception{
        log.info("Trying to obtain total day by weather");
        List<Tuple2<WeatherStatus, Integer>> weatherAndCantDays;

        try {
            weatherAndCantDays = galaxyWeatherDAO.findTotalDayByWeather();
            if(CollectionUtils.isEmpty(weatherAndCantDays)){
                log.warn("Not found total days for status");
            }
        } catch (Exception e) {
            log.error("An error ocurred trying to find total day by weather", e);
            throw new UnexpectedException("An error ocurred trying to find total day by weather", e);
        }

        return weatherAndCantDays;
    }


}
