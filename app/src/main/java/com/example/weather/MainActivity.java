package com.example.weather;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_LON = "detail:_lon";
    public static final String EXTRA_PARAM_LAT  = "detail:_lat";

    private TextView currentWeather, location, weatherDescription, latLon;
    private TextView tempMax, tempMin;

    FusedLocationProviderClient mFusedLocationClient;
    WeatherForecastAdapter adapter;
    RecyclerView weatherForecast;

    String lat;
    String lon;

    private ArrayList<ForecastItem> items = new ArrayList<>();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat = getIntent().getStringExtra(EXTRA_PARAM_LAT);
        lon = getIntent().getStringExtra(EXTRA_PARAM_LON);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        currentWeather = findViewById(R.id.currentWeather);
        location = findViewById(R.id.location);
        weatherDescription = findViewById(R.id.weatherDescription);
        tempMax = findViewById(R.id.tempMax);
        tempMin = findViewById(R.id.tempMin);
        latLon = findViewById(R.id.lat_lon);

        if (lat == null || lon == null) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, (Location location) -> {
                        if (location != null) {
                            String lat = location.getLatitude() + "";
                            String lon = location.getLongitude() + "";
                            loadGeneralCurrentData(lat, lon);

                            loadDailyWeather(lat, lon);
                        }
                    });
        } else {
            loadGeneralCurrentData(lat, lon);
            loadDailyWeather(lat, lon);
        }


        this.blurBottomSheet();

        weatherForecast = findViewById(R.id.recyclerView);
        weatherForecast.setHasFixedSize(true);
        // The number of Columns
        LinearLayoutManager layoutManager = new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false);
        weatherForecast.setLayoutManager(layoutManager);
        adapter = new WeatherForecastAdapter(this, items);
        weatherForecast.setAdapter(adapter);
    }

    public void loadDailyWeather(String lat, String lon) {
        WeatherAPI api = new WeatherAPI();
        api.getDailyWeather(lat, lon, new AsyncHttpClient.HTTPJSONResponse() {
            @Override
            public void onSuccess(JSONObject result) throws JSONException {
                JSONArray list = result.getJSONArray("list");

                items = new ArrayList<>();

                for (int i = 0; i < list.length(); i++) {
                    JSONObject item = (JSONObject) list.get(i);
                    JSONObject main = item.getJSONObject(("main"));
                    JSONObject weather = (JSONObject) item.getJSONArray("weather").get(0);
                    items.add(new ForecastItem(
                            item.getLong("dt"),
                            Utilities.toFahrenheitFromKelvin(main.getDouble("temp")),
                            weather.getString("icon")
                    ));
                }

                adapter = new WeatherForecastAdapter(MainActivity.this, items);
                weatherForecast.setAdapter(adapter);
            }

            @Override
            public void onException(Exception e) {

            }
        });
    }

    public void loadGeneralCurrentData(String lat, String lon) {
        WeatherAPI api = new WeatherAPI();

        api.getCurrentWeather(lat, lon, new AsyncHttpClient.HTTPJSONResponse() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    latLon.setText(
                            "Lat: "+ Utilities.round(4, Double.parseDouble(lat)
                            ) +" Lon: "+ Utilities.round(4, Double.parseDouble(lon))
                    );

                    JSONObject main = result.getJSONObject("main");
                    JSONObject weather = (JSONObject) result.getJSONArray("weather").get(0);

                    double temp = Double.parseDouble(main.getString("temp"));
                    currentWeather.setText(Utilities.toFahrenheitFromKelvin(temp) + "°");

                    weatherDescription.setText(Utilities.capitizeString(weather.getString("description")));
                    location.setText(result.getString("name"));

                    double tempMaxVal = Double.parseDouble(main.getString("temp_max"));
                    double tempMinVal = Double.parseDouble(main.getString("temp_min"));
                    tempMax.setText("H:" +Utilities.toFahrenheitFromKelvin(tempMaxVal) + "°");
                    tempMin.setText("L:" + Utilities.toFahrenheitFromKelvin(tempMinVal) + "°");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Exception e) {

            }
        });
    }

    public void blurBottomSheet() {
        float radius = 15f;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        BlurView blurView;
        blurView = findViewById(R.id.blurView);
        blurView.setupWith(rootView, new RenderEffectBlur()) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);

        blurView.setClipToOutline(true);
    }

}