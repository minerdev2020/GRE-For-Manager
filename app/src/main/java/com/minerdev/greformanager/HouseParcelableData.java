package com.minerdev.greformanager;

import android.os.Parcel;
import android.os.Parcelable;

public class HouseParcelableData implements Parcelable {
    public static final Creator<HouseParcelableData> CREATOR = new Creator<HouseParcelableData>() {
        @Override
        public HouseParcelableData createFromParcel(Parcel in) {
            return new HouseParcelableData(in);
        }

        @Override
        public HouseParcelableData[] newArray(int size) {
            return new HouseParcelableData[size];
        }
    };

    public int id;
    public String created_at;
    public String updated_at;
    public String address;
    public String number;
    public byte house_type;
    public byte facility;
    public byte payment_type;
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

    public HouseParcelableData() {

    }

    protected HouseParcelableData(Parcel in) {
        id = in.readInt();
        created_at = in.readString();
        updated_at = in.readString();
        address = in.readString();
        number = in.readString();
        house_type = in.readByte();
        facility = in.readByte();
        payment_type = in.readByte();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(address);
        dest.writeString(number);
        dest.writeByte(house_type);
        dest.writeByte(facility);
        dest.writeByte(payment_type);
        dest.writeInt(price);
        dest.writeInt(deposit);
        dest.writeInt(monthly_rent);
        dest.writeInt(premium);
        dest.writeInt(manage_fee);
        dest.writeString(manage_fee_contains);
        dest.writeFloat(area_meter);
        dest.writeFloat(rent_area_meter);
        dest.writeByte(building_floor);
        dest.writeByte(floor);
        dest.writeByte(structure);
        dest.writeByte(bathroom);
        dest.writeByte(bathroom_location);
        dest.writeByte(direction);
        dest.writeString(built_date);
        dest.writeString(move_date);
        dest.writeString(options);
        dest.writeString(detail_info);
        dest.writeString(phone);
        dest.writeByte(state);
    }
}