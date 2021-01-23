package com.minerdev.greformanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class FilterActivity extends AppCompatActivity {
    public static Filter filter = new Filter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
    }

    public static class Filter {
        private static HashMap<String, Boolean> checkedStates = new HashMap<>();
        private static String[] texts = {
                "원룸, 투룸", "원룸텔", "쉐어하우스",
                "빌라/주택", "오피스텔", "아파트", "상가/사무실",
                "월세", "전세", "매매", "단기임대",
                "원룸", "투룸", "쓰리룸 이상",
                "1층~5층", "6층 이상", "반지하", "옥탑", "복층",
                "5평 이하", "6~10평", "11평 이상",
                "신축", "풀옵션", "주차가능", "엘레베이터", "반려동물", "전세자금대출", "큰길가", "권리분석"
        };

        private Filter() {
            for (String text : texts) {
                checkedStates.put(text, false);
            }
        }

        public void setCheckState(String buttonText, boolean isChecked) {
            checkedStates.put(buttonText, isChecked);
        }
    }
}