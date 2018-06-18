package com.ml.weather.prediction.main.module;

import com.google.inject.servlet.ServletModule;
import com.ml.weather.prediction.front.route.FrontPagesRouter;
import com.ml.weather.prediction.front.route.WeatherPredictionRouter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RouterModule extends ServletModule {

    @Override
    protected void configureServlets(){
        bind(FrontPagesRouter.class).asEagerSingleton();
        bind(WeatherPredictionRouter.class).asEagerSingleton();

        Map<String, String> params = new HashMap<String, String>();
        serve("/*").with(GuiceContainer.class, params);
    }


}
