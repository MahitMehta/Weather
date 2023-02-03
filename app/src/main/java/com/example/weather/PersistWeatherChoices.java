package com.example.weather;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PersistWeatherChoices {
    private final static String WEATHER_CHOICES_KEY = "choices";

    private JSONArray choices;
    private Context ctx;
    private SharedPreferences preferences;

    public static class Choice {
        public double lat;
        public double lon;

        public Choice(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    public PersistWeatherChoices(Context ctx) throws JSONException {
         this.ctx = ctx;
         this.choices = new JSONArray();

         preferences = this.ctx.getSharedPreferences(
                "com.example.weather", Context.MODE_PRIVATE);
        if (preferences.contains(WEATHER_CHOICES_KEY)) {
            choices = new JSONObject(preferences.getString(WEATHER_CHOICES_KEY, getJSONObject().toString()))
                    .getJSONArray("choices");
        } else {
            preferences
                    .edit()
                    .putString(WEATHER_CHOICES_KEY, getJSONObject().toString())
                    .apply();
            choices = new JSONArray();
        }
    }

    public JSONObject getJSONObject() throws JSONException {
        return new JSONObject().put("choices", choices);
    }

    /**
     *
     * @param lat
     * @param lon
     * @return boolean value indicates whether the new item was added or not.
     * @throws JSONException
     */
    public boolean add(double lat, double lon) throws JSONException {
        if (alreadyHasChoice(lat, lon)) return false;
        this.choices.put(new JSONObject().put("lat", lat).put("lon", lon));
        saveChoice();
        return true;
    }

    public boolean alreadyHasChoice(double lat, double lon) throws JSONException {
        String latFuzz = Utilities.round(4, lat) + "";
        String lonFuzz = Utilities.round(4, lon) + "";

        for (int i = 0; i < choices.length(); i++) {
            JSONObject choice = (JSONObject) choices.get(i);
            String storedLatFuzz = Utilities.round(4, choice.getDouble("lat")) + "";
            String storedLonFuzz = Utilities.round(4, choice.getDouble("lon")) + "";

            if (storedLatFuzz.equals(latFuzz) && storedLonFuzz.equals(lonFuzz)) return true;
        }

        return false;
    }

    public ArrayList<Choice> getAll() throws JSONException {
        ArrayList<Choice> coords = new ArrayList<>();

        for (int i = 0; i < choices.length(); i++) {
            JSONObject savedCoord = (JSONObject) choices.get(i);
            coords.add(new Choice(
                    savedCoord.getDouble("lat"),
                    savedCoord.getDouble("lon")
            ));
        }

        return coords;
    }

    private void saveChoice() throws JSONException {
        preferences.edit().putString(WEATHER_CHOICES_KEY, getJSONObject().toString()).apply();
    }
}
