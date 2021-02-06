package com.minerdev.greformanager;

import android.os.Parcel;
import android.os.Parcelable;

public class House {
    public static class ParcelableData implements Parcelable {
        public int id;
        public String created_time;
        public String updated_time;

        public String address;
        public String house_number;
        public byte payment_type;
        public byte house_type;
        public byte facility;
        public int price;
        public int deposit;
        public int monthly_rent;
        public int premium;
        public int manage_fee;
        public String manage_fee_contains;
        public float area_meter;
        public float rent_area_meter;
        public byte building_floor;
        public byte floor;
        public byte structure;
        public byte bathroom;
        public byte bathroom_location;
        public byte direction;
        public String built_date;
        public String move_date;
        public String options;
        public String detail_info;
        public String phone;

        public byte state;

        protected ParcelableData(Parcel in) {
            id = in.readInt();
            created_time = in.readString();
            updated_time = in.readString();
            address = in.readString();
            house_number = in.readString();
            payment_type = in.readByte();
            house_type = in.readByte();
            facility = in.readByte();
            price = in.readInt();
            deposit = in.readInt();
            monthly_rent = in.readInt();
            premium = in.readInt();
            manage_fee = in.readInt();
            manage_fee_contains = in.readString();
            area_meter = in.readFloat();
            rent_area_meter = in.readFloat();
            building_floor = in.readByte();
            floor = in.readByte();
            structure = in.readByte();
            bathroom = in.readByte();
            bathroom_location = in.readByte();
            direction = in.readByte();
            built_date = in.readString();
            move_date = in.readString();
            options = in.readString();
            detail_info = in.readString();
            phone = in.readString();
            state = in.readByte();
        }

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

        public ParcelableData() {

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(created_time);
            parcel.writeString(updated_time);
            parcel.writeString(address);
            parcel.writeString(house_number);
            parcel.writeByte(payment_type);
            parcel.writeByte(house_type);
            parcel.writeByte(facility);
            parcel.writeInt(price);
            parcel.writeInt(deposit);
            parcel.writeInt(monthly_rent);
            parcel.writeInt(premium);
            parcel.writeInt(manage_fee);
            parcel.writeString(manage_fee_contains);
            parcel.writeFloat(area_meter);
            parcel.writeFloat(rent_area_meter);
            parcel.writeByte(building_floor);
            parcel.writeByte(floor);
            parcel.writeByte(structure);
            parcel.writeByte(bathroom);
            parcel.writeByte(bathroom_location);
            parcel.writeByte(direction);
            parcel.writeString(built_date);
            parcel.writeString(move_date);
            parcel.writeString(options);
            parcel.writeString(detail_info);
            parcel.writeString(phone);
            parcel.writeByte(state);
        }
    }
}
