package com.ml.weather.prediction.front.route;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;

import static spark.Spark.halt;
import spark.utils.IOUtils;

@Slf4j
@Path("weather")
public class FrontPagesRouter {

    private final String appContext;

    @Inject
    public FrontPagesRouter(@Named("project.appContext") String appContext) {
        this.appContext = appContext;
    }

    @GET
    public Object getHomePage() throws IOException {
        return writeFileToOutput("home.html");
    }

    private Object writeFileToOutput(String filePath) throws IOException {
        try (InputStream inputStream = FrontPagesRouter.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                halt(HttpStatus.NOT_FOUND_404, "Can't find file: '" + filePath + "'");
            }
            return IOUtils.toByteArray(inputStream);
        }
    }
}
