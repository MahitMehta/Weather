package com.example.weather;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;

import java.util.ArrayList;

public class WeatherChoicesActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private PersistWeatherChoices storage;
    private WeatherChoicesAdapter adapter;
    private ListView choicesListView;
    private FrameLayout search_results_sheet;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_choices_activity);

        try {
            storage = new PersistWeatherChoices(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        searchBar = findViewById(R.id.search_bar);
        choicesListView = findViewById(R.id.choices_list);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        search_results_sheet = findViewById(R.id.search_results_sheet);

        BottomSheetBehavior.from(search_results_sheet).setState(BottomSheetBehavior.STATE_HIDDEN);

        WeatherAPI api = new WeatherAPI();

        try {
            adapter = new WeatherChoicesAdapter(this, R.layout.weather_choice, storage.getAll());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        choicesListView.setAdapter(adapter);

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (v.getText().toString().equals("")) return true;
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(WeatherChoicesActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                api.getLocationsByZipCode(v.getText().toString(), new AsyncHttpClient.Response<ArrayList<WeatherAPI.Location>>() {
                    @Override
                    public void onSuccess(ArrayList<WeatherAPI.Location> result) {
                        if (result.size() >0) {
                            v.setText("");

                            Intent intent = new Intent(WeatherChoicesActivity.this, MainActivity.class);

                            double lat = result.get(0).lat;
                            double lon = result.get(0).lon;

                            addChoice(lat, lon);

                            intent.putExtra(MainActivity.EXTRA_PARAM_LAT, lat + "");
                            intent.putExtra(MainActivity.EXTRA_PARAM_LON, lon + "");

                            WeatherChoicesActivity.this.startActivity(intent);
                        } else {
                            CharSequence text = "Invalid Zip Code";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(WeatherChoicesActivity.this, text, duration);
                            toast.show();
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        CharSequence text = "Invalid Zip Code";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(WeatherChoicesActivity.this, text, duration);
                        toast.show();
                    }
                });
                handled = true;
            }
            return handled;
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts
                                    .RequestMultiplePermissions(), result -> {
                                Boolean coarseLocationGranted = result.getOrDefault(
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
                                if (coarseLocationGranted != null && coarseLocationGranted) {
                                    mFusedLocationClient.getLastLocation()
                                            .addOnSuccessListener(this, (Location location) -> {
                                                if (location != null) {

                                                    addChoice(location.getLatitude(), location.getLongitude());
                                                }
                                            });
                                } else {
                                    // No location access granted.
                                }
                            }
                    );

            locationPermissionRequest.launch(new String[] {
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            });

            return;
        }


    }

    public void addChoice(double lat, double lon)  {
        try {
            if (storage.add(lat, lon)) {
                adapter.add(new PersistWeatherChoices.Choice(
                        lat, lon
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
