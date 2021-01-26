package com.minerdev.greformanager;

import java.util.HashMap;

public class Filter {
    private HashMap<String, Boolean> checkedStates = new HashMap<>();
    private String[] texts = {
            "원룸, 투룸", "원룸텔", "쉐어하우스",
            "빌라/주택", "오피스텔", "아파트", "상가/사무실",
            "월세", "전세", "매매", "단기임대",
            "원룸", "투룸", "쓰리룸 이상",
            "1층~5층", "6층 이상", "반지하", "옥탑", "복층",
            "5평 이하", "6~10평", "11평 이상",
            "신축", "풀옵션", "주차가능", "엘레베이터", "반려동물", "전세자금대출", "큰길가", "권리분석"
    };

    private int depositMin;
    private int depositMax;

    private int monthlyRentMin;
    private int monthlyRentMax;
    private boolean isContainManageFee;

    public Filter() {
        for (String text : texts) {
            checkedStates.put(text, false);
        }
    }

    public void setCheckState(String buttonText, boolean isChecked) {
        checkedStates.put(buttonText, isChecked);
    }

    public void setDeposit(int min, int max) {
        depositMin = min;
        depositMax = max;
    }

    public void setMonthlyRent(boolean isContain, int min, int max) {
        isContainManageFee = isContain;
        monthlyRentMin = min;
        monthlyRentMax = max;
    }

    public Boolean getCheckedState(String key) {
        return checkedStates.get(key);
    }

    public HashMap<String, Boolean> getCheckedStates() {
        return checkedStates;
    }

    public int getDepositMin() {
        return depositMin;
    }

    public int getDepositMax() {
        return depositMax;
    }

    public boolean getIsContainManageFee() {
        return isContainManageFee;
    }

    public int getMonthlyRentMin() {
        return monthlyRentMin;
    }

    public int getMonthlyRentMax() {
        return monthlyRentMax;
    }
}
