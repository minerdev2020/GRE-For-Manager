package com.minerdev.greformanager;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class NetworkTask extends AsyncTask<Void, String, String> {
    private ProgressDialog dialog;

    private String url;
    private ContentValues values;
    private ContentValues header;
    private Context context;
    private RequestHttpURLConnection.RequestMethod requestMethod;
    private OnDataReceiveListener listener;

    public NetworkTask(Context context, String url, ContentValues values) {
        initialize(context, url, values, null);
    }

    public NetworkTask(Context context, String url, ContentValues values, ContentValues header) {
        initialize(context, url, values, header);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRequestMethod(RequestHttpURLConnection.RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setDialogMsg(String dialogMsg) {
        dialog.setMessage(dialogMsg);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result;
        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
        result = requestHttpURLConnection.request(requestMethod, url, values, header);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        dialog.dismiss();

        if (s == null) {
            Toast.makeText(context, "서버 연결 시간 초과!", Toast.LENGTH_SHORT).show();

        } else {
            listener.parseData(s);
        }
    }

    private void initialize(Context context, String url, ContentValues values, ContentValues header) {
        this.context = context;
        this.url = url;
        this.values = values;
        this.header = header;
        this.requestMethod = RequestHttpURLConnection.RequestMethod.GET;
        this.listener = new OnDataReceiveListener() {
            @Override
            public void parseData(String receivedString) {
                return;
            }
        };

        dialog = new ProgressDialog(this.context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("서버에 연결중...");
    }
}