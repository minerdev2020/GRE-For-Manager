package com.minerdev.greformanager;

public class House {
    private int uid;
    private String price;
    private String address;
    private State state;

    private HouseType houseType;
    private PaymentType paymentType;
    private int deposit;
    private int monthlyRent;
    private int roomCount;
    private int floor;
    private float area;
    private byte extra;

    public House(SerializedData serializedData) {
        setAll(serializedData);
    }

    public void setAll(SerializedData serializedData) {
        this.uid = serializedData.uid;
        this.price = serializedData.price;
        this.address = serializedData.address;
        this.state = State.values()[serializedData.state];

        this.houseType = HouseType.values()[serializedData.houseType];
        this.paymentType = PaymentType.values()[serializedData.paymentType];
        this.deposit = serializedData.deposit;
        this.monthlyRent = serializedData.monthlyRent;
        this.roomCount = serializedData.roomCount;
        this.floor = serializedData.floor;
        this.area = serializedData.area;
        this.extra = serializedData.extra;
    }

    public int getUid() {
        return uid;
    }

    public String getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public int getDeposit() {
        return deposit;
    }

    public int getMonthlyRent() {
        return monthlyRent;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public int getFloor() {
        return floor;
    }

    public float getArea() {
        return area;
    }

    public byte getExtra() {
        return extra;
    }

    public enum State {
        SALE("판매중"), SOLD_OUT("판매완료");

        private final String name;

        private State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum HouseType {
        VILLA("빌라/주택"), OFFICETELS("오피스텔"), APT("아파트"), OFFICE("상가/사무실");

        private final String name;

        private HouseType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum PaymentType {
        MONTHLY_RENT("월세"), JEONSE("전세"), BUY("매매"), SHORT_TERM_RENTAL("단기임대");

        private final String name;

        private PaymentType(String name) {
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
        public byte state;

        public byte houseType;
        public byte paymentType;
        public int deposit;
        public int monthlyRent;
        public byte roomCount;
        public byte floor;
        public float area;
        public byte extra;
    }
}
