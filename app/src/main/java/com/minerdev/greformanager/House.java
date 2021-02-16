package com.minerdev.greformanager;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class House {
    private final ParcelableData data;

    public House(ParcelableData data) {
        this.data = data;
    }

    public String getId() {
        return String.valueOf(data.id);
    }

    public String getCreatedAt() {
        return data.created_at;
    }

    public String getUpdatedAt() {
        return data.updated_at;
    }

    public String getAddress() {
        return data.address;
    }

    public String getNumber() {
        return data.number;
    }

    public String getPaymentType() {
        return Constants.getInstance().PAYMENT_TYPE.get(data.house_type - 1).get(data.payment_type);
    }

    public String getHouseType() {
        return Constants.getInstance().HOUSE_TYPE.get(data.house_type);
    }

    public boolean isFacility() {
        return data.facility == 1;
    }

    public String getPrice() {
        String strPrice;
        if (data.price == 0) {
            strPrice = parsePrice(data.deposit) + "/" + parsePrice(data.monthly_rent);

        } else {
            strPrice = parsePrice(data.price);
        }

        return strPrice;
    }

    public String getPremium() {
        return parsePrice(data.premium);
    }

    public String getManageFee() {
        return parsePrice(data.manage_fee);
    }

    public String getManageFeeContains() {
        return data.manage_fee_contains;
    }

    public String getAreaMeter() {
        return data.area_meter + "㎡";
    }

    @SuppressLint("DefaultLocale")
    public String getAreaPyeong() {
        return String.format("%.2f", data.area_meter * Constants.getInstance().METER_TO_PYEONG) + "평";
    }

    public String getRentAreaMeter() {
        return data.rent_area_meter + "㎡";
    }

    @SuppressLint("DefaultLocale")
    public String getRentAreaPyeong() {
        return String.format("%.2f", data.rent_area_meter * Constants.getInstance().METER_TO_PYEONG) + "평";
    }

    public String getArea() {
        return getAreaMeter() + "=" + getAreaPyeong();
    }

    public String getRentArea() {
        return getRentAreaMeter() + "=" + getRentAreaPyeong();
    }

    public String getBuildingFloor() {
        return data.building_floor + "층";
    }

    public String getFloor() {
        return data.floor > 0 ? data.floor + "층" : "반지하";
    }

    public String getStructure() {
        return Constants.getInstance().STRUCTURE.get(data.structure);
    }

    public String getBathroom() {
        return Constants.getInstance().BATHROOM.get(data.bathroom);
    }

    public String getBathroomLocation() {
        return data.bathroom_location == 0 ? "외부" : "내부";
    }

    public String getDirection() {
        return Constants.getInstance().DIRECTION.get(data.direction);
    }

    public String getBuiltDate() {
        return data.built_date;
    }

    public String getMoveDate() {
        return data.move_date.equals("") ? "즉시 입주 가능" : data.move_date;
    }

    public String getOptions() {
        return data.options;
    }

    public String getDetailInfo() {
        return data.detail_info;
    }

    public String getPhone() {
        return data.phone;
    }

    public boolean getState() {
        return data.state == Constants.getInstance().SOLD;
    }

    public void setState(boolean isSoldOut) {
        data.state = (byte) (isSoldOut ? Constants.getInstance().SOLD : Constants.getInstance().SALE);
    }

    public ParcelableData getData() {
        return data;
    }

    public String getBriefInfo() {
        String result = "전용 면적: " + getAreaPyeong();

        if (data.manage_fee > 0) {
            result += " | 관리비: " + getManageFee() + "만원";
        }

        if (data.structure > 0) {
            result += " | 구조: " + getStructure();
        }

        return result;
    }

    public String getHouseInfo() {
        return getHouseType() + " | 관리비 " + getManageFee() + "만원 | " + getStructure() + " | " + getFloor();
    }

    private String parsePrice(int price) {
        String result;
        int frontNumber = price / 10000;
        int backNumber = price - frontNumber * 10000;

        if (frontNumber > 0) {
            result = frontNumber + "억";
            result += (backNumber == 0) ? "" : " " + backNumber;

        } else {
            result = String.valueOf(price);
        }

        return result;
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

        protected ParcelableData(Parcel in) {
            id = in.readInt();
            created_at = in.readString();
            updated_at = in.readString();
            address = in.readString();
            number = in.readString();
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
            parcel.writeString(address);
            parcel.writeString(number);
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
