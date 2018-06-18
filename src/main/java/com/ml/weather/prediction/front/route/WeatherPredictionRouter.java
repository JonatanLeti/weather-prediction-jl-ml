package com.ml.weather.prediction.front.route;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.ml.weather.prediction.domain.WeatherStatus;
import com.ml.weather.prediction.services.GalaxyWeatherService;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("services")
public class WeatherPredictionRouter {

    private final GalaxyWeatherService galaxyWeatherService;
    private final Gson GSON = new GsonBuilder().create();


    @Inject
    public WeatherPredictionRouter(GalaxyWeatherService galaxyWeatherService) {
        this.galaxyWeatherService = galaxyWeatherService;
    }

    /**
     * curl example for find id DB:
     *              http://localhost:9290/services/weather?day=256
     *
     * curl example for recalculate:
     *              http://localhost:9290/services/weather?day=256&forceRecalculate=true
     */
    @GET
    @Path("/weather")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWeatherInDay(@QueryParam("day") Integer day, @QueryParam("forceRecalculate") Boolean forceRecalculate) throws Exception {
        if(forceRecalculate != null && forceRecalculate){
            return GSON.toJson(galaxyWeatherService.calculateWeatherInDay(day));
        }

        return GSON.toJson(galaxyWeatherService.findWeatherByDay(day));
    }

    /**
     * curl example for find id DB:
     *              http://localhost:9290/services/period?status=
     *                                                          RAIN
     *                                                          HEAVY_RAIN
     *                                                          DROUGHT
     *                                                          OPTIMUM
     *                                                          UNKNOWN

     */
    @GET
    @Path("/period")
    @Produces(MediaType.APPLICATION_JSON)
    public String findCantOfDayByWeather(@QueryParam("status") String status) throws Exception {
        return GSON.toJson(galaxyWeatherService.findWeatherPeriod(WeatherStatus.fromValue(StringUtils.upperCase(status))));
    }

    /**
     * curl example for find id DB:
     *              http://localhost:9290/services/all-period
     *
     */
    @GET
    @Path("/all-period")
    @Produces(MediaType.APPLICATION_JSON)
    public String findTotalDayByWeather() throws Exception {
        return GSON.toJson(galaxyWeatherService.findTotalDayByWeather());
    }

}