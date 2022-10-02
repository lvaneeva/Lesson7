package lesson7.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AccuWeatherProvider implements WeatherProvider {
    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECAST_ENDPOINT = "forecasts";
    private static final String CURRENT_CONDITIONS_ENDPOINT = "currentconditions";
    private static final String API_VERSION = "v1";
    private static final String API_KEY = ApplicationGlobalState.getInstance().getApiKey();

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String cityName;

    @Override
    public void getWeather(Periods periods) throws IOException {
        String cityKey = detectCityKey();
        if (periods.equals(Periods.NOW)) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host(BASE_HOST)
                    .addPathSegment(CURRENT_CONDITIONS_ENDPOINT)
                    .addPathSegment(API_VERSION)
                    .addPathSegment(cityKey)
                    .addQueryParameter("apikey", API_KEY)
                    .addQueryParameter("language", "ru-ru")
                    .addQueryParameter("metric", "true")
                    .build();

            Request request = new Request.Builder()
                    .addHeader("accept", "application/json")
                    .url(url)
                    .build();

            String response = client.newCall(request).execute().body().string();
            System.out.println(response);
            // TODO: ������� � ������ �/� ����� ����� �������� ��� ������������.
            //  ������� ����� WeatherResponse, ��������������� ����� ������� � ��������� ������
            //  ������� ������������ ������ ������� ����������� � C � ��������� (weather text)
            StringReader reader = new StringReader(response);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            WeatherResponse[] weatherResponse = objectMapper.readValue(reader, WeatherResponse[].class);
            System.out.println("� ������ " + cityName + " �� ���� " + weatherResponse[0].getLocalObservationDateTime().substring(0, 10) + " ��������� " + weatherResponse[0].getWeatherText() + ", ����������� - " + weatherResponse[0].getTemperature().getMetric().getValue());

        } else if (periods.equals(Periods.FIVE_DAYS)) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host(BASE_HOST)
                    .addPathSegment(FORECAST_ENDPOINT)
                    .addPathSegment(API_VERSION)
                    .addPathSegment("daily")
                    .addPathSegment("5day")
                    .addPathSegment(cityKey)
                    .addQueryParameter("apikey", "0d1tNZJPfzzT3qGokM18FGGxAUpt7hpj")
                    .addQueryParameter("language", "ru-ru")
                    .addQueryParameter("metric", "true")
                    .build();

            Request request = new Request.Builder()
                    .addHeader("accept", "application/json")
                    .url(url)
                    .build();

            String myresponse = client.newCall(request).execute().body().string();
            System.out.println(myresponse);
            // TODO: ������� � ������ �/� ����� ����� �������� ��� ������������.
            //  ������� ����� WeatherResponse, ��������������� ����� ������� � ��������� ������
            //  ������� ������������ ������ ������� ����������� � C � ��������� (weather text)
            StringReader reader = new StringReader(myresponse);
            ObjectMapper objectMapper = new ObjectMapper();
            Example example = objectMapper.readValue(reader, Example.class);

            for (DailyForecast dailyForecastList : example.getDailyForecasts()) {
                System.out.println("� ������ " + cityName + " �� ���� " + dailyForecastList.getDate().substring(0, 10) + " ��������� " + dailyForecastList.getDay().getIconPhrase() + ", ����������� - " + dailyForecastList.getTemperature().getMaximum().getValue());
            }
        }
    }

    public String detectCityKey() throws IOException {
        String selectedCity = ApplicationGlobalState.getInstance().getSelectedCity();

        HttpUrl detectLocationURL = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment("locations")
                .addPathSegment(API_VERSION)
                .addPathSegment("cities")
                .addPathSegment("autocomplete")
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("q", selectedCity)
                .build();

        Request request = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(detectLocationURL)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("���������� �������� ���������� � ������. " +
                    "��� ������ ������� = " + response.code() + " ���� ������ = " + response.body().string());
        }
        String jsonResponse = response.body().string();
        System.out.println("��������� ����� ������ " + selectedCity);

        if (objectMapper.readTree(jsonResponse).size() > 0) {
            cityName = objectMapper.readTree(jsonResponse).get(0).at("/LocalizedName").asText();
            String countryName = objectMapper.readTree(jsonResponse).get(0).at("/Country/LocalizedName").asText();
            System.out.println("������ ����� " + cityName + " � ������ " + countryName);
        } else throw new IOException("Server returns 0 cities");

        return objectMapper.readTree(jsonResponse).get(0).at("/Key").asText();
    }

}
