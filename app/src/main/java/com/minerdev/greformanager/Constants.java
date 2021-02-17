package com.minerdev.greformanager;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Constants {
    public final int SALE = 0;
    public final int SOLD = 1;
    public final float PYEONG_TO_METER = 3.305f;
    public final float METER_TO_PYEONG = 0.3025f;
    public final int HOUSE_DETAIL_ACTIVITY_REQUEST_CODE = 1;
    public final int HOUSE_MODIFY_ACTIVITY_REQUEST_CODE = 2;

    public final ArrayList<ArrayList<String>> PAYMENT_TYPE = new ArrayList<>();
    public final ArrayList<String> HOUSE_TYPE = new ArrayList<>();
    public final ArrayList<String> STRUCTURE = new ArrayList<>();
    public final ArrayList<String> DIRECTION = new ArrayList<>();
    public final ArrayList<String> BATHROOM = new ArrayList<>();

    public String DNS;

    private boolean isInitialized = false;

    private Constants() {

    }

    public static Constants getInstance() {
        return Holder.INSTANCE;
    }

    public void initialize(Context context) {
        if (!isInitialized) {
            Collections.addAll(HOUSE_TYPE, context.getResources().getStringArray(R.array.houseType));
            String[] temp = context.getResources().getStringArray(R.array.paymentType);
            for (int i = 0; i < HOUSE_TYPE.size() - 1; i++) {
                ArrayList<String> list = new ArrayList<>(Arrays.asList(temp[i].split(" ")));
                PAYMENT_TYPE.add(list);
            }

            Collections.addAll(STRUCTURE, context.getResources().getStringArray(R.array.structure));
            Collections.addAll(DIRECTION, context.getResources().getStringArray(R.array.direction));
            Collections.addAll(BATHROOM, context.getResources().getStringArray(R.array.bathroom));

            DNS = context.getResources().getString(R.string.local_server_dns);

            isInitialized = true;
        }
    }

    private static class Holder {
        public static final Constants INSTANCE = new Constants();
    }
}
