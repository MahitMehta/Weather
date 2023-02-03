package com.example.weather;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

public class AsyncHttpClient {
    public interface Response<T> {
        void onSuccess(T result);

        void onException(Exception e);
    }

    private interface Continuation<T> extends Response<T> {}

    public interface HTTPJSONResponse{
        void onSuccess(JSONObject result) throws JSONException;

        void onException(Exception e);
    }

    public static class RequestParams extends HashMap<String, String> {
        @Override
        public String toString() {
            String base = "?";
            String[] keys = this.keySet().toArray(new String[this.size()]);
            for (int i = 0; i < keys.length; i++) {
                base += keys[i] + "=" + this.get(keys[i]) + (i == keys.length - 1 ? "" : "&");
            }
            return base;
        }
    }

    private ApplicationExecutors execs;

    public AsyncHttpClient() {
        execs = new ApplicationExecutors();
    }

    private String getStringResponse(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString();
    }

    private void getHTTPRequest(String API_URL, Continuation<String> continuation) {
        execs.getBackground().execute(() -> {
            URL url;
            String data = null;
            Exception err = null;

            try {
                url = new URL(API_URL);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    data = getStringResponse(urlConnection);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                err = e;
            } catch (IOException e) {
                e.printStackTrace();
                err = e;
            }
            final String unparsedResponse = data;
            final Exception errorResponse = err;

            execs.getMainThread().execute(
                    () -> {
                        if (unparsedResponse != null) continuation.onSuccess(unparsedResponse);
                        else continuation.onException(errorResponse);
                    }
            );
        });
    }

    public void get(@NonNull String API_URL, HTTPJSONResponse response) {
        getHTTPRequest(API_URL, new Continuation<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    try {
                        response.onSuccess(data);
                    } catch (Exception e) {
                        response.onException(e);
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    response.onException(e);
                }

            }
            @Override
            public void onException(Exception e) {
                response.onException(e);
            }
        });
    }
}
