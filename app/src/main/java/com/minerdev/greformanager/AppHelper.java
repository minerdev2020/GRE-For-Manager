package com.minerdev.greformanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.android.volley.RequestQueue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class AppHelper {
    public RequestQueue requestQueue;

    private AppHelper() {

    }

    public static AppHelper getInstance() {
        return Holder.INSTANCE;
    }

    public String getPathFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }

    public byte[] getByteArrayFromUri(Context context, Uri uri) {
        File file = new File(getPathFromUri(context, uri));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int size;

        if (file.exists() && file.canRead()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                while ((size = inputStream.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, size);
                }

                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static class Holder {
        public static final AppHelper INSTANCE = new AppHelper();
    }
}
