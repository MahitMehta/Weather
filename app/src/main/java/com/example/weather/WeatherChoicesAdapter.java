package com.example.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class WeatherChoicesAdapter extends ArrayAdapter<PersistWeatherChoices.Choice> {
    int xmlResource;
    List<PersistWeatherChoices.Choice> list;
    Context ctx;

    public WeatherChoicesAdapter(@NonNull Context ctx, int resource, @NonNull List<PersistWeatherChoices.Choice> objects) {
        super(ctx, resource, objects);
        xmlResource = resource;
        list = objects;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        PersistWeatherChoices.Choice group = list.get(position);

        WeatherAPI api = new WeatherAPI();

        TextView temp = adapterLayout.findViewById(R.id.choice_temp);
        TextView location = adapterLayout.findViewById(R.id.choice_location);
        TextView description = adapterLayout.findViewById(R.id.choice_description);
        TextView tempHighLow = adapterLayout.findViewById(R.id.choice_temp_high_low);
        ImageView weather_icon = adapterLayout.findViewById(R.id.choice_weather_icon);

        api.getCurrentWeather(group.lat + "", group.lon + "", new AsyncHttpClient.HTTPJSONResponse() {
            @Override
            public void onSuccess(JSONObject result) throws JSONException {
                JSONObject main = result.getJSONObject("main");
                temp.setText((int)Utilities.toFahrenheitFromKelvin(main.getDouble("temp")) + "°");

                int tempHigh = (int) Utilities.toFahrenheitFromKelvin(main.getDouble("temp_max"));
                int tempLow = (int) Utilities.toFahrenheitFromKelvin(main.getDouble("temp_min"));
                tempHighLow.setText("H:"+tempHigh+"° L:"+tempLow+"°");

                JSONObject weather = (JSONObject) result.getJSONArray("weather").get(0);
                description.setText(Utilities.capitizeString(weather.getString("description")));

                String icon_code = weather.getString("icon");
                weather_icon.setImageResource(Utilities.weatherPictureByCode(icon_code));

                String name = result.getString("name");
                String country = result.getJSONObject("sys").getString("country");
                location.setText(name + ", " + country);
            }

            @Override
            public void onException(Exception e) {

            }
        });

        adapterLayout.setOnClickListener((View v) -> {
            Intent intent = new Intent(ctx, MainActivity.class);

            intent.putExtra(MainActivity.EXTRA_PARAM_LAT, group.lat + "");
            intent.putExtra(MainActivity.EXTRA_PARAM_LON, group.lon + "");


            ctx.startActivity(intent);


        });

        return adapterLayout;
    }
}