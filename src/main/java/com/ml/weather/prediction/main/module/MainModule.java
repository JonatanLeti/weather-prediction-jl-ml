package com.ml.weather.prediction.main.module;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.ml.weather.prediction.main.Main;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        loadEnvironmentalProperties();
    }

    private void loadEnvironmentalProperties() {
        try {
            log.info("Loading default properties");
            Properties properties = new Properties();
            properties.load(Main.class.getResourceAsStream("/application.properties"));
            Names.bindProperties(binder(), properties);

        } catch (IOException e) {
            throw new RuntimeException("Can not read property file(s)", e);
        }

    }
}
