package com.minerdev.greformanager;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "image")
public class ImageParcelableData implements Parcelable {
    @Ignore
    public static final Creator<ImageParcelableData> CREATOR = new Creator<ImageParcelableData>() {
        @Override
        public ImageParcelableData createFromParcel(Parcel in) {
            return new ImageParcelableData(in);
        }

        @Override
        public ImageParcelableData[] newArray(int size) {
            return new ImageParcelableData[size];
        }
    };

    @PrimaryKey
    public int id;
    public String created_at;
    public String updated_at;
    public String title;
    public String path;
    public byte position;
    public int house_id;

    public ImageParcelableData() {

    }

    protected ImageParcelableData(Parcel in) {
        id = in.readInt();
        created_at = in.readString();
        updated_at = in.readString();
        title = in.readString();
        path = in.readString();
        position = in.readByte();
        house_id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
        parcel.writeString(title);
        parcel.writeString(path);
        parcel.writeByte(position);
        parcel.writeInt(house_id);
    }
}
