package com.minerdev.greformanager;

import java.util.ArrayList;

/**
* y는 latitude, 즉 위도,
* x는 longitude, 즉 경도.
* **/
public class GeocodeResult {
    public String status;
    public Meta meta;
    public ArrayList<Address> addresses;
    public String errorMessage;

    public class Meta {
        public Integer totalCount;
        public Integer page;
        public Integer count;
    }

    public class Address {
        public String roadAddress;
        public String jibunAddress;
        public String englishAddress;
        public ArrayList<AddressElement> addressElements;
        public String x; // latitude
        public String y; // longitude
        public Double distance;
    }

    public class AddressElement {
        public ArrayList<String> types;
        public String longName;
        public String shortName;
        public String code;
    }
}
