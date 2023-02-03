package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderEffectBlur;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {
    private ArrayList<ForecastItem> items;
    private Context context;

    public WeatherForecastAdapter(Context context, ArrayList<ForecastItem> items) {
        super();

        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.forecast_item, viewGroup, false);

        ViewHolder holder = new ViewHolder(v, context, items.get(i));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastItem item = items.get(position);


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder   {
        Context context;
        ForecastItem item;
        TextView temp;
        TextView time;
        ImageView forecast_icon;

        ViewHolder(View itemView, Context context, ForecastItem item) {
            super(itemView);
            this.context = context;
            this.item = item;

            temp = itemView.findViewById(R.id.forecast_temp);
            time = itemView.findViewById(R.id.forecast_time);
            forecast_icon = itemView.findViewById(R.id.forecast_icon);

            forecast_icon.setImageResource(Utilities.weatherPictureByCode(item.getIconCode()));

            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(item.getTime(), 0, ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h a", Locale.ENGLISH);
            String formattedDate = dateTime
                    .atZone(ZoneOffset.UTC)
                    .withZoneSameInstant(ZoneOffset.systemDefault())
                    .toLocalDateTime().format(formatter);

            time.setText(formattedDate);

            blurBackground();

            temp.setText(item.getTemperature() + "°");
        }

        private void blurBackground() {
            float radius = 25f;

            View decorView = ((MainActivity)context).getWindow().getDecorView();
            ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();

            BlurView blurView;
            blurView = itemView.findViewById(R.id.forecast_blurView);
            blurView.setupWith(rootView, new RenderEffectBlur()) // or RenderEffectBlur
                    .setFrameClearDrawable(windowBackground) // Optional
                    .setBlurRadius(radius);

            blurView.setClipToOutline(true);
        }
    }
}
