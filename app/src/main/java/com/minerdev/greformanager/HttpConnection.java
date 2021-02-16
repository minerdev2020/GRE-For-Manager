package com.minerdev.greformanager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
        String serverUri = context.getResources().getString(R.string.local_server_dns) + "/" + uri;
        Log.d("SEND_DATA", serverUri);
        makeRequest(context, Request.Method.GET, serverUri, null, listener);
    }

    public void send(Context context, int method, String uri, JsonObject data, OnReceiveListener listener) {
        String serverUri = context.getResources().getString(R.string.local_server_dns) + "/" + uri;
        String json = data.toString();
        Log.d("SEND_DATA", json);
        makeRequest(context, method, serverUri, json, listener);
    }

    public void send(Context context, int method, String uri, House.ParcelableData data, OnReceiveListener listener) {
        String serverUri = context.getResources().getString(R.string.local_server_dns) + "/" + uri;
        Gson gson = new Gson();
        String json = gson.toJson(data);
        Log.d("SEND_DATA", json);
        makeRequest(context, method, serverUri, json, listener);
    }

    public void send(Context context, int method, String uri, ArrayList<Uri> imageUris, OnReceiveListener listener) {
        String serverUri = context.getResources().getString(R.string.local_server_dns) + "/" + uri;
//        for (Uri imageUri : imageUris) {
//            Log.d("SEND_DATA", imageUri.getPath());
//            makeImageRequest(context, method, serverUri, imageUri, listener);
//        }
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
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    private void makeImageRequest(Context context, int method, String uri, Uri imageUri, OnReceiveListener listener) {
        Bitmap bitmap = getBitmapImage(context, imageUri);
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, uri,
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
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imageName = System.currentTimeMillis();
                params.put("image", new DataPart(imageName + ".png", getFileDataFromDrawable(bitmap)));
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

    private Bitmap getBitmapImage(Context context, Uri imageUri) {
        Bitmap bitmap = null;
        ContentResolver resolver = context.getContentResolver();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public interface OnReceiveListener {
        void onReceive(String receivedData);
    }

    private static class Holder {
        public static final HttpConnection INSTANCE = new HttpConnection();
    }

    //    private String createCopyAndReturnRealPath(Context context, Uri uri) {
//        final ContentResolver contentResolver = context.getContentResolver();
//
//        if (contentResolver == null) {
//            return null;
//        }
//
//        String filePath = context.getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();
//        File file = new File(filePath);
//        try {
//            InputStream inputStream = contentResolver.openInputStream(uri);
//            if (inputStream == null) {
//                return null;
//            }
//
//            OutputStream outputStream = new FileOutputStream(file);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) > 0) {
//                outputStream.write(buffer, 0, length);
//            }
//
//            outputStream.close();
//            inputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        return file.getAbsolutePath();
//    }
}

