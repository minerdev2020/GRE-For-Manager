package com.minerdev.greformanager;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity
public class Image implements Parcelable {
    @Ignore
    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @PrimaryKey
    public int id;
    public Timestamp created_at;
    public Timestamp updated_at;
    public String title;
    public String path;
    public byte position;
    public byte thumbnail;
    public int house_id;

    public Image() {

    }

    protected Image(Parcel in) {
        id = in.readInt();
        created_at = Timestamp.valueOf(in.readString());
        updated_at = Timestamp.valueOf(in.readString());
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
        parcel.writeString(created_at.toString());
        parcel.writeString(updated_at.toString());
        parcel.writeString(title);
        parcel.writeString(path);
        parcel.writeByte(position);
        parcel.writeInt(house_id);
    }
}
