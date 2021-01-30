package com.minerdev.greformanager;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestHttpURLConnection {
    public String request(RequestMethod requestMethod, String urlString, ContentValues values) {
        return request(requestMethod, urlString, values, null);
    }

    public String request(RequestMethod requestMethod, String urlString, ContentValues values, ContentValues headers) {
        HttpURLConnection urlConn = null;
        try {
            if (requestMethod == RequestMethod.GET && values != null && values.size() > 0) {
                urlString += "?" + getValuesInSingleLine(values);
            }

            URL url = new URL(urlString);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod(requestMethod.getName());
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");

            if (requestMethod == RequestMethod.POST) {
                String strParams = getValuesInSingleLine(values);
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, Object> parameter : headers.valueSet()) {
                    String key = parameter.getKey();
                    String value = parameter.getValue().toString();
                    urlConn.setRequestProperty(key, value);
                }
            }

            Log.d("Http", "from client : " + url.toString());

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            String line;
            String page = "";
            while ((line = reader.readLine()) != null) {
                page += line;
            }

            Log.d("Http", "from server : " + page);

            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();

        } catch (IOException e) { // for openConnection().
            e.printStackTrace();

        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;
    }

    private String getValuesInSingleLine(ContentValues values) {
        StringBuffer stringBuffer = new StringBuffer();
        if (values == null) {
            stringBuffer.append("");

        } else {
            boolean isAnd = false;
            for (Map.Entry<String, Object> parameter : values.valueSet()) {
                String key = parameter.getKey();
                String value = parameter.getValue().toString();

                if (isAnd)
                    stringBuffer.append("&");

                stringBuffer.append(key + "=" + value);

                if (!isAnd)
                    if (values.size() >= 2)
                        isAnd = true;
            }
        }

        return stringBuffer.toString();
    }

    public enum RequestMethod {
        GET("GET"), POST("POST");

        private final String name;

        private RequestMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
