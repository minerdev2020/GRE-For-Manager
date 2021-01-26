package com.minerdev.greformanager;

import java.util.ArrayList;
import java.util.HashMap;

public class Filter {
    private static final byte[] FLAGS = {
            (byte) 0x0000000F, (byte) 0x000000F0, (byte) 0x00000F00, (byte) 0x0000F000,
            (byte) 0x000F0000, (byte) 0x00F00000, (byte) 0x0F000000, (byte) 0xF0000000
    };

    // 각 탭 페이지의 토글 버튼 텍스트
    private static final HashMap<String, ArrayList<String>> toggleButtonTexts = new HashMap<>();
//    private static String[] texts = {
//            "빌라/주택", "오피스텔", "아파트", "상가/사무실",
//            "월세", "전세", "매매", "단기임대",
//            "원룸", "투룸", "쓰리룸 이상",
//            "1층~5층", "6층 이상", "반지하", "옥탑", "복층",
//            "5평 이하", "6~10평", "11평 이상",
//            "신축", "풀옵션", "주차가능", "엘레베이터", "반려동물", "전세자금대출", "큰길가", "권리분석"}
//};

    // 각 토글 버튼의 토글 상태
    private final HashMap<String, Boolean> checkedStates = new HashMap<>();

    private int depositMin;
    private int depositMax;

    private int monthlyRentMin;
    private int monthlyRentMax;
    private boolean isContainManageFee;

    public static void addMenuTitle(String tabPageTitle, String text) {
        if (toggleButtonTexts.get(tabPageTitle) == null) {
            toggleButtonTexts.put(tabPageTitle, new ArrayList<>());
        }

        toggleButtonTexts.get(tabPageTitle).add(text);
    }

    public static HashMap<String, ArrayList<String>> getToggleButtonTexts() {
        return toggleButtonTexts;
    }

    public static ArrayList<String> getToggleButtonTextsInPage(String tabPageTitle) {
        return toggleButtonTexts.get(tabPageTitle);
    }

    public void Initialize() {
        for (ArrayList<String> list : toggleButtonTexts.values()) {
            for (String text : list) {
                checkedStates.put(text, false);
            }
        }

        depositMin = 0;
        depositMax = 0;
        monthlyRentMin = 0;
        monthlyRentMax = 0;
        isContainManageFee = false;
    }

    public boolean isMatch(House house) {
        if (house == null) {
            return false;
        }

        if (getCheckedState(house.getHouseType().getName())
                && getCheckedState(house.getPaymentType().getName())
                && (house.getDeposit() >= depositMin && house.getDeposit() <= depositMax)
                && (house.getMonthlyRent() >= monthlyRentMin && house.getDeposit() <= monthlyRentMax)
                && checkRoomCount(house.getRoomCount())
                && checkFloor(house.getFloor())
                && checkArea(house.getArea())
                && checkExtra(house.getExtra())
        ) {
            return true;

        } else {
            return false;
        }
    }

    public String getCheckedTextsInSingleLine(String tabPageTitle) {
        String result = "";
        ArrayList<String> list = toggleButtonTexts.get(tabPageTitle);
        for (String buttonText : list) {
            if (checkedStates.get(buttonText)) {
                if (!result.equals("")) {
                    result += " | ";
                }

                result += buttonText;
            }
        }

        return result;
    }

    public Boolean getCheckedState(String buttonText) {
        return checkedStates.get(buttonText);
    }

    public Boolean getCheckedState(String tabPageTitle, int position) {
        String buttonText = toggleButtonTexts.get(tabPageTitle).get(position);
        return checkedStates.get(buttonText);
    }

    public void setCheckedState(String buttonText, boolean isChecked) {
        checkedStates.put(buttonText, isChecked);
    }

    public void setCheckedState(String tabPageTitle, int position, boolean isChecked) {
        String buttonText = toggleButtonTexts.get(tabPageTitle).get(position);
        checkedStates.put(buttonText, isChecked);
    }

    public int getDepositMin() {
        return depositMin;
    }

    public int getDepositMax() {
        return depositMax;
    }

    public void setDeposit(int min, int max) {
        depositMin = min;
        depositMax = max;
    }

    public int getMonthlyRentMin() {
        return monthlyRentMin;
    }

    public int getMonthlyRentMax() {
        return monthlyRentMax;
    }

    public void setMonthlyRent(boolean isContain, int min, int max) {
        isContainManageFee = isContain;
        monthlyRentMin = min;
        monthlyRentMax = max;
    }

    public boolean getIsContainManageFee() {
        return isContainManageFee;
    }

    private boolean checkFlag(byte flag, int position) {
        if ((flag & FLAGS[position]) == 0) {
            return false;

        } else {
            return true;
        }
    }

    private boolean checkRoomCount(int roomCount) {
        int index = getRoomCountIndex(roomCount);
        return getCheckedState("방 개수", index);
    }

    private boolean checkFloor(int floor) {
        int index = getFloorIndex(floor);
        return getCheckedState("층수", index);
    }

    private boolean checkArea(float area) {
        int index = getAreaIndex(area);
        return getCheckedState("평수", index);
    }

    private boolean checkExtra(byte extra) {
        int size = getToggleButtonTextsInPage("추가옵션").size();
        for (int i = 0; i < size; i++) {
            if (checkFlag(extra, i) != getCheckedState("추가옵션", i)) {
                return false;
            }
        }

        return true;
    }

    private int getRoomCountIndex(int roomCount) {
        if (roomCount == 1) {
            return 0;

        } else if (roomCount == 2) {
            return 1;

        } else {
            return 2;
        }
    }

    private int getFloorIndex(int floor) {
        if (floor == -100) {
            return 3;

        } else if (floor == -99) {
            return 4;

        } else if (floor <= 0) {
            return 2;

        } else if (floor <= 5) {
            return 0;

        } else {
            return 1;
        }
    }

    private int getAreaIndex(float area) {
        if (area <= 5) {
            return 0;

        } else if (area <= 10) {
            return 1;

        } else {
            return 2;
        }
    }
}
