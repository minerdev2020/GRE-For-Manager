package com.minerdev.greformanager;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.net.URLEncoder;

public class Geocoding {
    private final Point point = new Point();

    private Geocoding() {
    }

    public static Geocoding getInstance() {
        return GeocodingHolder.INSTANCE;
    }

    public void getPointFromNaver(Context context, String client_id, String client_secret, String address) {
        point.address = address;

        String clientId = client_id;
        String clientSecret = client_secret;
        try {
            address = URLEncoder.encode(address, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";

        ContentValues values = new ContentValues();
        values.put("query", address);

        ContentValues headers = new ContentValues();
        headers.put("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.put("X-NCP-APIGW-API-KEY", clientSecret);

        NetworkTask networkTask = new NetworkTask(context, apiURL, values, headers);
        networkTask.setDialogMsg("좌표를 얻어오는중...");
        networkTask.setOnDataReceiveListener(new OnDataReceiveListener() {
            @Override
            public void parseData(String receivedString) {
                Gson gson = new Gson();
                NaverData data = new NaverData();
                try {
                    data = gson.fromJson(receivedString, NaverData.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (data.result != null) {
                    point.x = data.result.items.get(0).point.x;
                    point.y = data.result.items.get(0).point.y;
                    point.havePoint = true;
                }

                Log.d("Http", "x : " + point.x + ", y : " + point.y);
            }
        });
        networkTask.execute();
    }

    public Point getPoint() {
        return point;
    }

    private static class GeocodingHolder {
        public static final Geocoding INSTANCE = new Geocoding();
    }

    public class Point {
        // 위도
        public double x;

        // 경도
        public double y;

        public String address;

        // 포인트를 받았는지 여부
        public boolean havePoint;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("x : ");
            builder.append(x);
            builder.append(" y : ");
            builder.append(y);
            builder.append(" address : ");
            builder.append(address);

            return builder.toString();
        }
    }
}
