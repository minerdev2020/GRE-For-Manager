package com.minerdev.greformanager;

import java.util.HashMap;

public class Filter {
    private static final byte[] FLAGS = {
            (byte) 0x0000000F, (byte) 0x000000F0, (byte) 0x00000F00, (byte) 0x0000F000,
            (byte) 0x000F0000, (byte) 0x00F00000, (byte) 0x0F000000, (byte) 0xF0000000
    };

    private static HashMap<String, HashMap<String, Boolean>> checkedStates = new HashMap<>();

    private static int depositMin;
    private static int depositMax;
    private static int monthlyRentMin;
    private static int monthlyRentMax;
    private static boolean isContainManageFee;

    public static void addCheckedStates(String tabTitle, HashMap<String, Boolean> checkedStates) {
        Filter.checkedStates.put(tabTitle, checkedStates);
    }

    public static int getDepositMin() {
        return depositMin;
    }

    public static int getDepositMax() {
        return depositMax;
    }

    public static int getMonthlyRentMin() {
        return monthlyRentMin;
    }

    public static int getMonthlyRentMax() {
        return monthlyRentMax;
    }

    public static boolean isIsContainManageFee() {
        return isContainManageFee;
    }

    public static void setDeposit(int depositMin, int depositMax) {
        Filter.depositMin = depositMin;
        Filter.depositMax = depositMax;
    }

    public static void setMonthlyRen(boolean isContainManageFee, int monthlyRentMin, int monthlyRentMax) {
        Filter.isContainManageFee = isContainManageFee;
        Filter.monthlyRentMin = monthlyRentMin;
        Filter.monthlyRentMax = monthlyRentMax;
    }

    public static boolean isMatch(House house) {
        if (house == null) {
            return false;
        }

        if (getCheckedState("건물형태", house.getHouseType().getName())
                && getCheckedState("매물종류", house.getPaymentType().getName())
                && (house.getDeposit() >= depositMin && house.getDeposit() <= depositMax)
                && (house.getMonthlyRent() >= monthlyRentMin && house.getDeposit() <= monthlyRentMax)
                && (house.isContainManageFee() == isContainManageFee)
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

    private static boolean checkFlag(byte flag, int position) {
        if ((flag & FLAGS[position]) == 0) {
            return false;

        } else {
            return true;
        }
    }

    private static boolean checkRoomCount(int roomCount) {
        return getCheckedState("방 개수", getRoomCountIndex(roomCount));
    }

    private static boolean checkFloor(int floor) {
        return getCheckedState("층수", getFloorIndex(floor));
    }

    private static boolean checkArea(float area) {
        return getCheckedState("평수", getAreaIndex(area));
    }

    private static boolean checkExtra(byte extra) {
        if (checkedStates.size() == 0) {
            return true;
        }

        if (checkedStates.get("추가옵션") == null) {
            return true;
        }

        int i = 0;
        for (Boolean state : checkedStates.get("추가옵션").values()) {
            if (state == null || checkFlag(extra, i) != state) {
                return false;
            }

            i++;
        }

        return true;
    }

    private static String getRoomCountIndex(int roomCount) {
        if (roomCount == 1) {
            return "원룸";

        } else if (roomCount == 2) {
            return "투룸";

        } else {
            return "쓰리룸 이상";
        }
    }

    private static String getFloorIndex(int floor) {
        if (floor == -100) {
            return "옥탑";

        } else if (floor == -99) {
            return "복층";

        } else if (floor <= 0) {
            return "반지하";

        } else if (floor <= 5) {
            return "1층~5층";

        } else {
            return "6층 이상";
        }
    }

    private static String getAreaIndex(float area) {
        if (area <= 5) {
            return "5평 이하";

        } else if (area <= 10) {
            return "6~10평";

        } else {
            return "11평 이상";
        }
    }

    private static boolean getCheckedState(String tabTitle, String key) {
        if (checkedStates.size() == 0) {
            return true;
        }

        if (checkedStates.get(tabTitle) == null) {
            return true;
        }

        return checkedStates.get(tabTitle).get(key);
    }

    private static boolean getCheckedState(String key) {
        if (checkedStates.size() == 0) {
            return true;
        }

        Boolean result = false;
        for (HashMap<String, Boolean> list : checkedStates.values()) {
            if (list == null) {
                continue;
            }

            Boolean temp = list.get(key);
            if (temp == null) {
                continue;
            }

            if (temp == true) {
                result = true;
                break;
            }
        }

        return result;
    }
}
