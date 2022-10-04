package lesson7.project;

import java.io.IOException;

public interface WeatherProvider {
    void getWeather(Periods periods) throws IOException;
    void getAllFromDb() throws IOException;
}
