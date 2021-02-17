package com.minerdev.greformanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpConnection {
    private HttpConnection() {

    }

    public static HttpConnection getInstance() {
        return Holder.INSTANCE;
    }

    public void receive(Context context, String uri, OnReceiveListener listener) {
        String serverUri = Constants.getInstance().DNS + "/" + uri;
        Log.d("SEND_DATA", serverUri);
        makeRequest(context, Request.Method.GET, serverUri, null, listener);
    }

    public void send(Context context, int method, String uri, JsonObject data, OnReceiveListener listener) {
        String serverUri = Constants.getInstance().DNS + "/" + uri;
        String json = data.toString();
        Log.d("SEND_DATA", json);
        makeRequest(context, method, serverUri, json, listener);
    }

    public void send(Context context, int method, String uri, House.ParcelableData data, OnReceiveListener listener) {
        String serverUri = Constants.getInstance().DNS + "/" + uri;
        Gson gson = new Gson();
        String json = gson.toJson(data);
        Log.d("SEND_DATA", json);
        makeRequest(context, method, serverUri, json, listener);
    }

    public void send(Context context, int method, String uri, ArrayList<Uri> imageUris, OnReceiveListener listener) {
        String serverUri = Constants.getInstance().DNS + "/" + uri;
        int position = 0;
        for (Uri imageUri : imageUris) {
            Log.d("SEND_DATA", imageUri.getPath());
            makeImageRequest(context, method, serverUri, imageUri, position, listener);
            position++;
        }
    }

    private void makeRequest(Context context, int method, String uri, String json, OnReceiveListener listener) {
        StringRequest request = new StringRequest(method, uri,
                response -> {
                    Toast.makeText(context, "데이터 전송 성공.", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onReceive(response);
                    }
                },
                error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                if (json == null || json.equals(""))
                    return super.getBody();

                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                if (json == null || json.equals(""))
                    return super.getBodyContentType();

                return "application/json";
            }
        };

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }


    private void makeImageRequest(Context context, int method, String uri, Uri imageUri, int position, OnReceiveListener listener) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File file = new File(getPathFromUri(context, imageUri));
        FileInputStream inputStream;
        byte[] buf = new byte[1024];
        int size;

        if (file.exists() && file.canRead()) {
            try {
                inputStream = new FileInputStream(file);
                while ((size = inputStream.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, size);
                }

                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String title = System.currentTimeMillis() + ".jpg";
        VolleyMultipartRequest request = new VolleyMultipartRequest(method, uri,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("position", String.valueOf(position));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(title, byteArrayOutputStream.toByteArray()));
                return params;
            }
        };

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public String getPathFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }

    public interface OnReceiveListener {
        void onReceive(String receivedData);
    }

    private static class Holder {
        public static final HttpConnection INSTANCE = new HttpConnection();
    }
}

