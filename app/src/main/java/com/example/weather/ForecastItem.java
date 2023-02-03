package com.example.weather;

public class ForecastItem {
    private long time;
    private double temperature;
    private String iconCode;

    public ForecastItem(long time, double temperature, String iconCode) {
        this.iconCode = iconCode;
        this.time = time;
        this.temperature = temperature;
    }

    public long getTime() {
        return time;
    }

    public String getIconCode() { return this.iconCode; }

    public double getTemperature() {
        return temperature;
    }

    public String toString() {
        return "Time: " + this.time;
    }
}
