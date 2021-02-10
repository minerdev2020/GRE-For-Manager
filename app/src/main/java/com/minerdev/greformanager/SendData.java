package com.minerdev.greformanager;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SendData {
    private SendData() {

    }

    public static SendData getInstance() {
        return Holder.INSTANCE;
    }

    public void start(Context context, HashMap<String, Object> data, String uri) {
        String jsonUri = context.getResources().getString(R.string.web_server_dns) + "/" + uri;

        Gson gson = new Gson();
        String json = gson.toJson(data);
        Log.d("SENDDATA", json);
        sendJson(context, jsonUri, json);
    }

    public void start(Context context, House.ParcelableData data, String uri) {
        String jsonUri = context.getResources().getString(R.string.web_server_dns) + "/" + uri;

        Gson gson = new Gson();
        String json = gson.toJson(data);
        Log.d("SENDDATA", json);
        sendJson(context, jsonUri, json);
    }

    public void start(Context context, House.ParcelableData data, ArrayList<Uri> imageUris, String uri) {
        String jsonUri = context.getResources().getString(R.string.web_server_dns) + "/" + uri;

        Gson gson = new Gson();
        String json = gson.toJson(data);
        Log.d("SENDDATA", json);
        sendJson(context, jsonUri, json);

//        for (Uri imageUri : imageUris) {
//            sendImage(context, jsonUri, imageUri);
//        }
    }

    private void sendJson(Context context, String address, String json) {
        StringRequest request = new StringRequest(Request.Method.POST, address,
                response -> Toast.makeText(context, "데이터 전송 성공.", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("house_value", json);

                return params;
            }
        };

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);

        AppHelper.requestQueue.add(request);
    }

    private void sendImage(Context context, String address, Uri imageUri) {
        StringRequest request = new StringRequest(Request.Method.POST, address,
                response -> Toast.makeText(context, "데이터 전송 성공.", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return super.getBody();
            }
        };

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);

        AppHelper.requestQueue.add(request);
    }

    private String createCopyAndReturnRealPath(Context context, Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null) {
            return null;
        }

        String filePath = context.getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();
        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null) {
                return null;
            }

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file.getAbsolutePath();
    }

    private static class Holder {
        public static final SendData INSTANCE = new SendData();
    }
}

