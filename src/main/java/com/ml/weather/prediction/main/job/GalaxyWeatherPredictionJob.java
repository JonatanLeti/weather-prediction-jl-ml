package com.ml.weather.prediction.main.job;

import com.ml.weather.prediction.domain.GalaxyWeatherDTO;
import com.ml.weather.prediction.domain.WeatherStatus;
import com.ml.weather.prediction.services.GalaxyWeatherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.jooq.lambda.Seq;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class GalaxyWeatherPredictionJob extends Thread{

    private final GalaxyWeatherService galaxyWeatherService;
    private final int yearsToPredict;
    private final int daysPerYear;

    @Inject
    public GalaxyWeatherPredictionJob(GalaxyWeatherService galaxyWeatherService,
                                      @Named("job.galaxy.weather.prediction.in.years") int yearsToPredict,
                                      @Named("job.galaxy.weather.prediction.days.per.year") int daysPerYear) {

        this.galaxyWeatherService = galaxyWeatherService;
        this.yearsToPredict = yearsToPredict;
        this.daysPerYear = daysPerYear;
    }

    @Override
    public void run() {
        List<GalaxyWeatherDTO> periodWeatherList = new ArrayList<>();
        int totalDaysToCalculate = yearsToPredict * daysPerYear;

        log.info("START weather prediction for next {} year - ({} days)", yearsToPredict, totalDaysToCalculate);
        StopWatch time = new StopWatch();
        time.start();

        for (int day = 0; day < totalDaysToCalculate; day++) {
            GalaxyWeatherDTO galaxyWeatherDTO = this.galaxyWeatherService.calculateWeatherInDay(day);
            periodWeatherList.add(galaxyWeatherDTO);
        }

        //filtra la lista por clima 'LLuvia'
        //ordeno por area del triangulo
        //reverse para ordenar de mayor a menor
        //tomo el primero (que es el de mayor valor)
        Seq.seq(periodWeatherList)
                .filter(Objects::nonNull)
                .filter(p -> WeatherStatus.RAIN.equals(p.getWeatherStatus()))
                .sorted(GalaxyWeatherDTO::getPlanetsTriangleArea)
                .reverse()
                .findFirst()
                .ifPresent(galaxyWeatherMaxRain -> galaxyWeatherMaxRain.setWeatherStatus(WeatherStatus.HEAVY_RAIN));

        Seq.seq(periodWeatherList).forEach(p -> {
            try{
                boolean saved = this.galaxyWeatherService.saveNewGalaxyWeatherDTO(p);
                log.info("Saved weather {} RESULT: {}", p, saved);
            }catch (Exception e){
                log.error("An error ocurred trying to save weather {}", p.toString(), e);
                // TODO: notify?
            }
        });

        time.stop();
        log.info("END weather prediction - TOTAL TIME ms {}", time.getTime(TimeUnit.MILLISECONDS));
    }

}
