package com.ml.weather.prediction.db;

import com.ml.weather.prediction.domain.GalaxyWeatherDTO;
import com.ml.weather.prediction.domain.PlanetsPosition;
import com.ml.weather.prediction.domain.WeatherStatus;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.DeleteDbFiles;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Singleton
public class GalaxyWeatherDAO {

    private final String DB_DRIVER;
    private final String DB_CONNECTION;
    private final String DB_USER;
    private final String DB_PASSWORD;

    @Inject
    public GalaxyWeatherDAO(@Named("h2.db.driver") String dbDriver,
                            @Named("h2.db.connection") String dbConnection,
                            @Named("h2.db.user") String dbUser,
                            @Named("h2.db.pass") String dbPassword) {

        this.DB_DRIVER = dbDriver;
        this.DB_CONNECTION = dbConnection;
        this.DB_USER = dbUser;
        this.DB_PASSWORD = dbPassword;

        this.initialize();
    }

    private void initialize() {
        DeleteDbFiles.execute("~", "test", true);
        this.createTable();
    }

    public boolean saveNewGalaxyWeatherDTO(GalaxyWeatherDTO dto) {
        boolean saved = false;
        Statement stmt;
        Random r = new Random();

        try {
            Connection connection = getDBConnection();

            if (connection != null) {
                connection.setAutoCommit(false);
                stmt = connection.createStatement();
                saved = stmt.execute("INSERT INTO GalaxyWeather(" +
                        " id," +
                        " day," +
                        " xPositionFerengi," +
                        " yPositionFerengi," +
                        " xPositionVulcano," +
                        " yPositionVulcano," +
                        " xPositionBetasoide," +
                        " yPositionBetasoide," +
                        " planetsTriangleArea," +
                        " weatherStatus," +
                        " planetsPosition" +
                        " ) VALUES(" +
                        r.nextInt() + ", " +
                        dto.getDay() + ", " +
                        dto.getXPositionFerengi() + ", " +
                        dto.getYPositionFerengi() + ", " +
                        dto.getXPositionVulcano() + ", " +
                        dto.getYPositionVulcano() + ", " +
                        dto.getXPositionBetasoide() + ", " +
                        dto.getYPositionBetasoide() + ", " +
                        dto.getPlanetsTriangleArea() + ", " +
                        "'" + dto.getWeatherStatus() + "', " +
                        "'" + dto.getPlanetsPosition() + "') "
                );

                stmt.close();
                connection.commit();
            }

        } catch (SQLException e) {
            log.error("Exception Message {}", e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }


        return saved;
    }

    public GalaxyWeatherDTO findWeatherByDay(int day) throws SQLException {
        PreparedStatement st = null;
        GalaxyWeatherDTO dto = null;

        try {
            Connection connection = this.getDBConnection();

            if (connection != null) {
                st = connection.prepareStatement("SELECT * FROM GalaxyWeather WHERE day = ?");
                st.setInt(1, day);

                ResultSet rs = st.executeQuery();

                if (rs.next()) {
                    dto = GalaxyWeatherDTO.builder()
                            .id(rs.getBigDecimal("id"))
                            .day(rs.getInt("day"))
                            .xPositionFerengi(rs.getDouble("xPositionFerengi"))
                            .yPositionFerengi(rs.getDouble("yPositionFerengi"))
                            .xPositionVulcano(rs.getDouble("xPositionVulcano"))
                            .yPositionVulcano(rs.getDouble("yPositionVulcano"))
                            .xPositionBetasoide(rs.getDouble("xPositionBetasoide"))
                            .yPositionBetasoide(rs.getDouble("yPositionBetasoide"))
                            .planetsTriangleArea(rs.getDouble("planetsTriangleArea"))
                            .weatherStatus(WeatherStatus.valueOf(rs.getString("weatherStatus")))
                            .planetsPosition(PlanetsPosition.valueOf(rs.getString("planetsPosition")))
                            .build();
                }

            }
        } finally {
            if (st != null) st.close();
        }


        return dto;
    }

    public List<GalaxyWeatherDTO> findWeatherPeriod(WeatherStatus weatherStatus) throws SQLException {
        PreparedStatement st = null;
        List<GalaxyWeatherDTO> weatherDTOList = new ArrayList<>();
        try {
            Connection connection = this.getDBConnection();

            if (connection != null) {
                st = connection.prepareStatement("SELECT * FROM GalaxyWeather WHERE weatherStatus = ?");
                st.setString(1, weatherStatus.value());

                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    weatherDTOList.add(GalaxyWeatherDTO.builder()
                            .id(rs.getBigDecimal("id"))
                            .day(rs.getInt("day"))
                            .xPositionFerengi(rs.getDouble("xPositionFerengi"))
                            .yPositionFerengi(rs.getDouble("yPositionFerengi"))
                            .xPositionVulcano(rs.getDouble("xPositionVulcano"))
                            .yPositionVulcano(rs.getDouble("yPositionVulcano"))
                            .xPositionBetasoide(rs.getDouble("xPositionBetasoide"))
                            .yPositionBetasoide(rs.getDouble("yPositionBetasoide"))
                            .planetsTriangleArea(rs.getDouble("planetsTriangleArea"))
                            .weatherStatus(WeatherStatus.valueOf(rs.getString("weatherStatus")))
                            .planetsPosition(PlanetsPosition.valueOf(rs.getString("planetsPosition")))
                            .build());
                }

            }

        } finally {
            if (st != null) st.close();
        }

        return weatherDTOList;
    }

    public List<Tuple2<WeatherStatus, Integer>> findTotalDayByWeather() throws SQLException {
        PreparedStatement st = null;
        List<Tuple2<WeatherStatus, Integer>> totalDayByWeather = new ArrayList<>();
        try {
            Connection connection = this.getDBConnection();

            if (connection != null) {
                st = connection.prepareStatement("SELECT weatherStatus status, count(id) totalDays FROM GalaxyWeather GROUP BY weatherStatus");

                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    totalDayByWeather.add(
                            Tuple.tuple(
                                    WeatherStatus.valueOf(rs.getString("weatherStatus")),
                                    rs.getInt("totalDays")));
                }
            }

        } finally {
            if (st != null) st.close();
        }

        return totalDayByWeather;
    }

    private void createTable() {
        Statement stmt;

        try {
            Connection connection = getDBConnection();

            if(connection != null){
                connection.setAutoCommit(false);
                stmt = connection.createStatement();

                PreparedStatement st = connection.prepareStatement("DROP TABLE IF EXISTS GalaxyWeather");
                st.executeUpdate();

                stmt.execute("CREATE TABLE GalaxyWeather(" +
                        " id int primary key," +
                        " day int," +
                        " xPositionFerengi double," +
                        " yPositionFerengi double," +
                        " xPositionVulcano double," +
                        " yPositionVulcano double," +
                        " xPositionBetasoide double," +
                        " yPositionBetasoide double," +
                        " planetsTriangleArea double," +
                        " weatherStatus varchar(50)," +
                        " planetsPosition varchar(50)" +
                        " )");

                log.info("H2 Database create TABLE GalaxyWeather");

                stmt.close();
                connection.commit();

            }
        } catch (SQLException e) {
            log.error("Exception Message " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }

    private Connection getDBConnection() {
        Connection dbConnection;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            log.error("{}", e.getMessage(), e);
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            log.error("{}", e.getMessage(), e);
        }
        return null;
    }
}
