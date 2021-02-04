package com.minerdev.greformanager;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Geocode {
    private GeocodeResult result;
    private OnDataReceiveListener listener;

    private Geocode() {

    }

    public static Geocode getInstance() {
        return GeocodeHolder.INSTANCE;
    }

    public void setOnDataReceiveListener(OnDataReceiveListener listener) {
        this.listener = listener;
    }

    public void getQueryResponseFromNaver(Context context, String address) {
        String apiURL = context.getString(R.string.naver_open_api_geocode) + "?query=" + address;
        StringRequest request = new StringRequest(Request.Method.GET, apiURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Gson gson = new Gson();
                            result = gson.fromJson(response, GeocodeResult.class);

                            if (result != null) {
                                listener.parseData(result);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String client_id = context.getString(R.string.client_id);
                String client_secret = context.getString(R.string.client_secret);

                Map<String, String> params = new HashMap<String, String>();
                params.put(context.getString(R.string.api_key_id_header), client_id);
                params.put(context.getString(R.string.api_key_header), client_secret);

                return params;
            }
        };

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);

        AppHelper.requestQueue.add(request);
    }

    public interface OnDataReceiveListener {
        void parseData(GeocodeResult result);
    }

    private static class GeocodeHolder {
        public static final Geocode INSTANCE = new Geocode();
    }
}
