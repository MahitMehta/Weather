package com.example.weather;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherAPI {
    private static final String CURRENT_WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String ZIP_WEATHER_BASE_URL = "https://api.openweathermap.org/geo/1.0/zip";

    AsyncHttpClient client;

    public WeatherAPI() {
        client = new AsyncHttpClient();
    }

    private static final String API_KEY = BuildConfig.OPEN_WEATHER_API_KEY;

    public void getCurrentWeather(String lat, String lon, AsyncHttpClient.HTTPJSONResponse response) {
        AsyncHttpClient.RequestParams params = new AsyncHttpClient.RequestParams();

        params.put("lat", lat);
        params.put("lon", lon);
        params.put("appid", API_KEY);

        client.get(CURRENT_WEATHER_BASE_URL + params, response);
    }

    public static class Location {
        public double lat;
        public double lon;
        public String name;

        public Location(double lat, double lon, String name) {
            this.name = name; this.lat = lat; this.lon = lon;
        }
    }

    public void getLocationsByZipCode(String query, AsyncHttpClient.Response<ArrayList<Location>> response) {
        AsyncHttpClient.RequestParams params = new AsyncHttpClient.RequestParams();

        params.put("zip", query);
        params.put("appid", API_KEY);

        ArrayList<Location> locations = new ArrayList<>();

        client.get(ZIP_WEATHER_BASE_URL + params, new AsyncHttpClient.HTTPJSONResponse() {
            @Override
            public void onSuccess(JSONObject result) throws JSONException {
                if (result.has("cod")
                        && result.getString("cod").startsWith("4")
                ){
                    response.onSuccess(locations);
                    return;
                }

                locations.add(new Location(
                        result.getDouble("lat"),
                        result.getDouble("lon"),
                        result.getString("name")
                ));
                response.onSuccess(locations);
            }

            @Override
            public void onException(Exception e) {

                response.onException(e);
            }
        });
    }

    public void getDailyWeather(String lat, String lon, AsyncHttpClient.HTTPJSONResponse response) {
        AsyncHttpClient.RequestParams params = new AsyncHttpClient.RequestParams();

        params.put("lat", lat);
        params.put("lon", lon);
        params.put("cnt", "7");
        params.put("appid", API_KEY);

        client.get(FORECAST_WEATHER_BASE_URL + params, response);
    }
}
