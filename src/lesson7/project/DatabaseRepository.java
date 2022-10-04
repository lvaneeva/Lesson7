package lesson7.project;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseRepository {
    boolean saveWeatherData(WeatherData weatherData) throws SQLException;

    List<WeatherData> getAllSavedData() throws SQLException;

}
