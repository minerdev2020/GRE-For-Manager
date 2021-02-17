package com.minerdev.greformanager;

import android.os.Parcel;
import android.os.Parcelable;

public class Image {
    private final ParcelableData data;

    public Image(ParcelableData data) {
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

    public int getHouseId() {
        return data.house_id;
    }

    public ParcelableData getData() {
        return data;
    }

    public static class ParcelableData implements Parcelable {
        public static final Creator<ParcelableData> CREATOR = new Creator<ParcelableData>() {
            @Override
            public ParcelableData createFromParcel(Parcel in) {
                return new ParcelableData(in);
            }

            @Override
            public ParcelableData[] newArray(int size) {
                return new ParcelableData[size];
            }
        };

        public int id;
        public String created_at;
        public String updated_at;
        public String title;
        public String path;
        public byte position;
        public int house_id;

        protected ParcelableData(Parcel in) {
            id = in.readInt();
            created_at = in.readString();
            updated_at = in.readString();
            title = in.readString();
            path = in.readString();
            position = in.readByte();
            house_id = in.readInt();
        }

        public ParcelableData() {

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
}
