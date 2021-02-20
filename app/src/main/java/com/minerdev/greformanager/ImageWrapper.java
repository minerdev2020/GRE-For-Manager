package com.minerdev.greformanager;

import android.net.Uri;

public class ImageWrapper {
    private final Image data;

    public ImageWrapper(Image data) {
        this.data = data;
    }

    public int getId() {
        return data.id;
    }

    public String getCreatedAt() {
        return data.created_at;
    }

    public String getUpdatedAt() {
        return data.updated_at;
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
