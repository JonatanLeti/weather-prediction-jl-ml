package com.ml.weather.prediction.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.ml.weather.prediction.main.job.GalaxyWeatherPredictionJob;
import com.ml.weather.prediction.main.module.MainModule;
import com.ml.weather.prediction.main.module.RouterModule;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

@Slf4j
public class Main {

    private static final int DEFAULT_PORT = 9290;

    public Main() {
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(DEFAULT_PORT);

        final Injector injector = Guice.createInjector(new MainModule(), new RouterModule());

        ServletContextHandler sch = new ServletContextHandler(server, "/");

        sch.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injector;
            }
        });

        sch.addFilter(GuiceFilter.class, "/*", null);
        sch.addServlet(DefaultServlet.class, "/");

        server.start();

        GalaxyWeatherPredictionJob galaxyWeatherPredictionJob = injector.getInstance(GalaxyWeatherPredictionJob.class);
        galaxyWeatherPredictionJob.start();
        galaxyWeatherPredictionJob.join();

        server.join();
    }
}
