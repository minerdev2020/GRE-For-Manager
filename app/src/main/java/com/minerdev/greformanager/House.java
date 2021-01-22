package com.minerdev.greformanager;

public class House {
    private int uid;
    private String price;
    private String address;
    private State state;
    private House_Type houseType;
    private Payment_Type paymentType;

    public House(SerializedData serializedData) {
        this.uid = serializedData.uid;
        this.price = serializedData.price;
        this.address = serializedData.address;
        this.state = State.values()[serializedData.state];
        this.houseType = House_Type.values()[serializedData.houseType];
        this.paymentType = Payment_Type.values()[serializedData.paymentType];
    }

    public int getUid() {
        return uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public House_Type getHouseType() {
        return houseType;
    }

    public void setHouseType(House_Type houseType) {
        this.houseType = houseType;
    }

    public Payment_Type getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Payment_Type paymentType) {
        this.paymentType = paymentType;
    }

    public enum Payment_Type {
        SALE("매매"), MONTHLY_RENT("월세"), JEONSE("전세"), SHORT_TERM_RENTAL("단기임대");

        private final String name;

        private Payment_Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum State {
        Sale("판매중"), SOLD_OUT("판매완료");

        private final String name;

        private State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum House_Type {
        APT("아파트"), VILLA("빌라/주택"), OFFICETELS("오피스텔"), OFFICE("상가/사무실");

        private final String name;

        private House_Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class SerializedData {
        public int uid;
        public String price;
        public String address;
        public int state;
        public int houseType;
        public int paymentType;
    }
}
