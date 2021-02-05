package com.minerdev.greformanager;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Constants {
    public final int SALE = 0;
    public final int SOLD_OUT = 1;
    public final ArrayList<String> HOUSE_TYPE = new ArrayList<>();
    public final ArrayList<ArrayList<String>> PAYMENT_TYPE = new ArrayList<>();

    private Constants() {

    }

    public static Constants getInstance() {
        return Holder.INSTANCE;
    }

    public void initialize(Context context) {
        Collections.addAll(HOUSE_TYPE, context.getResources().getStringArray(R.array.houseType));
        HOUSE_TYPE.remove(0);

        String[] temp = context.getResources().getStringArray(R.array.paymentType);
        for (int i = 0; i < HOUSE_TYPE.size() - 1; i++) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(temp[i].split(" ")));
            list.remove(0);
            PAYMENT_TYPE.add(list);
        }
    }

    private static class Holder {
        public static final Constants INSTANCE = new Constants();
    }
}
