package com.minerdev.greformanager;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReceiveData {
    private ReceiveData() {

    }

    public static ReceiveData getInstance() {
        return Holder.INSTANCE;
    }

    public void start(Context context, OnReceiveListener listener) {
        String uri = context.getResources().getString(R.string.web_server_dns) + "/selectDB.php";
        receiveJson(context, uri, listener);
    }

    private void receiveJson(Context context, String address, OnReceiveListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, address,
                response -> {
                    Toast.makeText(context, "데이터 요청 성공.", Toast.LENGTH_SHORT).show();

                    Gson gson = new Gson();
                    House.ParcelableData[] array = gson.fromJson(response, House.ParcelableData[].class);
                    if (array == null) {
                        Toast.makeText(context, "데이터 수신 실패.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<House.ParcelableData> list = new ArrayList<>();
                    Collections.addAll(list, array);

                    if (listener != null) {
                        listener.onReceive(list);
                    }
                },
                error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data_type", "house_list");

                return params;
            }
        };

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);

        AppHelper.requestQueue.add(request);
    }

    private static class Holder {
        public static final ReceiveData INSTANCE = new ReceiveData();
    }

    public interface OnReceiveListener {
        void onReceive(ArrayList<House.ParcelableData> list);
    }
}
