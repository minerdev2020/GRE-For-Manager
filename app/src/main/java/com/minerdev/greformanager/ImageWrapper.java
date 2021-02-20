package com.minerdev.greformanager;

import android.net.Uri;

import java.sql.Date;

public class ImageWrapper {
    private final Image data;

    public ImageWrapper(Image data) {
        this.data = data;
    }

    public int getId() {
        return data.id;
    }

    public String getCreatedAt() {
        Date date = new Date(data.created_at);
        return date.toString();
    }

    public String getUpdatedAt() {
        Date date = new Date(data.updated_at);
        return date.toString();
    }

    public String getTitle() {
        return data.title;
    }

    public String getPath() {
        return data.path;
    }

    public byte getPosition() {
        return data.position;
    }

    public byte getThumbnail() {
        return data.thumbnail;
    }

    public int getHouseId() {
        return data.house_id;
    }

    public Uri getUri(int position) {
        return Uri.parse(Constants.getInstance().DNS + "/storage/images/" + data.house_id + "/" + data.title);
    }

    public Image getData() {
        return data;
    }
}
