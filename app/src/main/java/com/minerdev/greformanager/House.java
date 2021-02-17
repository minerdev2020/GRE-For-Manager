package com.minerdev.greformanager;

import android.annotation.SuppressLint;

public class House {
    private final HouseParcelableData data;

    public House(HouseParcelableData data) {
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
        return (data.move_date == null || data.move_date.equals("")) ? "즉시 입주 가능" : data.move_date;
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

    public HouseParcelableData getData() {
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
}
