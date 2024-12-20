package ru.feryafox.Task2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Scanner;

public class WeatherApp {

    private static final String API_KEY = "КЛЮЧ";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/current.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите название города:");
        String city = scanner.nextLine();

        try {
            String weatherData = fetchWeatherData(city);
            WeatherResponse weatherResponse = parseWeatherData(weatherData);
            displayWeather(weatherResponse);
        } catch (IOException e) {
            System.err.println("Ошибка при получении данных о погоде: " + e.getMessage());
        }
    }

    private static String fetchWeatherData(String city) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%s?key=%s&q=%s&aqi=no", BASE_URL, API_KEY, city);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    private static WeatherResponse parseWeatherData(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, WeatherResponse.class);
    }

    private static void displayWeather(WeatherResponse weatherResponse) {
        String cityName = weatherResponse.getLocation().getName();
        double temperatureCelsius = weatherResponse.getCurrent().getTempC();
        String conditionText = weatherResponse.getCurrent().getCondition().getText();

        System.out.printf("Погода в %s:%nТемпература: %.1f°C%nОблачность: %s%n", cityName, temperatureCelsius, conditionText);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherResponse {
        private Location location;
        private Current current;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Current getCurrent() {
            return current;
        }

        public void setCurrent(Current current) {
            this.current = current;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        @JsonProperty("temp_c")
        private double tempC;

        private Condition condition;

        public double getTempC() {
            return tempC;
        }

        public void setTempC(double tempC) {
            this.tempC = tempC;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Condition {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
