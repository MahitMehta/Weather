package com.example.weather;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utilities {
    public static double toFahrenheitFromKelvin(double temp, int round) {
        return round(round, (9/5d) * (temp - 273.15) + 32);
    }

    public static double toFahrenheitFromKelvin(double temp) {
        return toFahrenheitFromKelvin(temp, 2);
    }

    public static double round(int place, double num) {
        return Math.round(num * Math.pow(10, place)) / Math.pow(10, place);
    }

    public static String capitizeString(String name){
        String captilizedString= Arrays.stream(name.split(" ")).map((String str) -> {
            return str.substring(0,1).toUpperCase() + str.substring(1);
        }).collect(Collectors.joining(" "));
        return captilizedString;
    }
    public static int weatherPictureByCode(String code) {
        switch (code) {
            case "01n":
                return R.drawable.clear_sky_night;
            case "02n":
                return R.drawable.few_clouds_night;
            case "03n":
                return R.drawable.scattered_clouds_night;
            case "04n":
            case "04d":
                return R.drawable.cloudy_day;
            case "09n":
            case "10n":
                return R.drawable.rain_night;
            case "11n":
            case "11d":
                return R.drawable.thunder;
            case "13n":
            case "13d":
                return R.drawable.snow;
            case "02d":
                return R.drawable.few_clouds_day;
            case "03d":
                return R.drawable.scattered_clouds_day;
            case "09d":
            case "10d":
                return R.drawable.rain_day;
            case "50d":
            case "50n":
                return R.drawable.mist;
            default: return R.drawable.clear_sky_day;
        }
    }
}
