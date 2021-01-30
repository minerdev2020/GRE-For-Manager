package com.minerdev.greformanager;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.HashMap;

public class Geocode {
    private GeocodeResult result;
    private OnDataReceiveCallback listener;

    private Geocode() {

    }

    public static Geocode getInstance() {
        return GeocodeHolder.INSTANCE;
    }

    public void setOnDataReceiveCallback(OnDataReceiveCallback listener) {
        this.listener = listener;
    }

    public void getPointFromNaver(Context context, String address) {
        try {
            address = URLEncoder.encode(address, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        String apiURL = context.getString(R.string.naver_open_api_geocode);

        ContentValues values = new ContentValues();
        values.put("query", address);

        ContentValues headers = new ContentValues();

        String client_id = context.getString(R.string.client_id);
        String client_secret = context.getString(R.string.client_secret);

        headers.put(context.getString(R.string.api_key_id_header), client_id);
        headers.put(context.getString(R.string.api_key_header), client_secret);

        NetworkTask networkTask = new NetworkTask(context, apiURL, values, headers);
        networkTask.setDialogMsg("좌표를 얻어오는중...");
        networkTask.setOnDataReceiveCallback(new NetworkTask.OnDataReceiveCallback() {
            @Override
            public void parseData(String receivedString) {
                try {
                    Gson gson = new Gson();
                    result = gson.fromJson(receivedString, GeocodeResult.class);

                    if (result != null) {
                        listener.parseData(result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkTask.execute();
    }

    public interface OnDataReceiveCallback {
        void parseData(GeocodeResult result);
    }

    private static class GeocodeHolder {
        public static final Geocode INSTANCE = new Geocode();
    }
}
