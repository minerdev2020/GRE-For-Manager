package com.minerdev.greformanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.internal.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterActivity extends AppCompatActivity {
    private ArrayList<ToggleButtonGroup> toggleButtonGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setToggleButtonGroups();
        loadCheckedStates();

        Button button_apply = findViewById(R.id.filter_button_apply);
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilter();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.filter_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.filter_menu_reset:
                resetFilter();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToggleButtonGroups() {
        toggleButtonGroups = new ArrayList<>();

        ToggleButtonGroup toggleButtonGroup1 = new ToggleButtonGroup(this, "건물형태");
        toggleButtonGroup1.addToggleButtons("빌라/주택", "오피스텔", "아파트", "상가/사무실");
        toggleButtonGroups.add(toggleButtonGroup1);
        setFLowLayout(R.id.filter_layout_1, toggleButtonGroup1);

        ToggleButtonGroup toggleButtonGroup2 = new ToggleButtonGroup(this, "매물종류");
        toggleButtonGroup2.addToggleButtons("월세", "전세", "매매", "단기임대");
        toggleButtonGroups.add(toggleButtonGroup2);
        setFLowLayout(R.id.filter_layout_2, toggleButtonGroup2);

        ToggleButtonGroup toggleButtonGroup5 = new ToggleButtonGroup(this, "방 개수");
        toggleButtonGroup5.addToggleButtons("원룸", "투룸", "쓰리룸 이상");
        toggleButtonGroups.add(toggleButtonGroup5);
        setFLowLayout(R.id.filter_layout_5, toggleButtonGroup5);

        ToggleButtonGroup toggleButtonGroup6 = new ToggleButtonGroup(this, "층수");
        toggleButtonGroup6.addToggleButtons("1층~5층", "6층 이상", "반지하", "옥탑", "복층");
        toggleButtonGroups.add(toggleButtonGroup6);
        setFLowLayout(R.id.filter_layout_6, toggleButtonGroup6);

        ToggleButtonGroup toggleButtonGroup7 = new ToggleButtonGroup(this, "평수");
        toggleButtonGroup7.addToggleButtons("5평 이하", "6~10평", "11평 이상");
        toggleButtonGroups.add(toggleButtonGroup7);
        setFLowLayout(R.id.filter_layout_7, toggleButtonGroup7);

        ToggleButtonGroup toggleButtonGroup8 = new ToggleButtonGroup(this, "추가옵션");
        toggleButtonGroup8.addToggleButtons("신축", "풀옵션", "주차가능", "엘레베이터", "반려동물", "전세자금대출", "큰길가", "권리분석");
        toggleButtonGroups.add(toggleButtonGroup8);
        setFLowLayout(R.id.filter_layout_8, toggleButtonGroup8);
    }

    private void setFLowLayout(int id, ToggleButtonGroup group) {
        FlowLayout layout = findViewById(id);
        ArrayList<ToggleButton> list = group.getToggleButtons();
        for (ToggleButton button : list) {
            layout.addView(button);
        }
    }

    private void loadCheckedStates() {
        // 보증금
        TextView deposit_textView = findViewById(R.id.deposit_textView);
        EditText deposit_max = findViewById(R.id.deposit_editText_max);
        EditText deposit_min = findViewById(R.id.deposit_editText_min);

        deposit_textView.setText(Filter.getDepositMin() + "원 ~ " + Filter.getDepositMax() + "원");
        deposit_max.setText(String.valueOf(Filter.getDepositMax()));
        deposit_min.setText(String.valueOf(Filter.getDepositMin()));

        // 월세금
        TextView monthly_rent_textView = findViewById(R.id.monthly_rent_textView);
        EditText monthly_rent_max = findViewById(R.id.monthly_rent_editText_max);
        EditText monthly_rent_min = findViewById(R.id.monthly_rent_editText_min);
        CheckBox checkBox = findViewById(R.id.monthly_rent_checkBox);

        monthly_rent_textView.setText(Filter.getMonthlyRentMin() + "원 ~ " + Filter.getMonthlyRentMax() + "원");
        monthly_rent_max.setText(String.valueOf(Filter.getMonthlyRentMax()));
        monthly_rent_min.setText(String.valueOf(Filter.getMonthlyRentMin()));
        checkBox.setChecked(Filter.isIsContainManageFee());

        // 나머지 사항들
        for (ToggleButtonGroup group : toggleButtonGroups) {
            if (group != null) {
                group.initCheckedStates(Filter.getCheckedStates(group.getTitle()));
            }
        }
    }

    private void saveFilter() {
        // 보증금
        EditText deposit_max = findViewById(R.id.deposit_editText_max);
        EditText deposit_min = findViewById(R.id.deposit_editText_min);

        String minStr = deposit_min.getText().toString();
        String maxStr = deposit_max.getText().toString();

        int minValue = minStr.equals("") ? 0 : Integer.parseInt(minStr);
        int maxValue = maxStr.equals("") ? Integer.MAX_VALUE : Integer.parseInt(maxStr);
        Filter.setDeposit(minValue, maxValue);

        // 월세금
        EditText monthly_rent_max = findViewById(R.id.monthly_rent_editText_max);
        EditText monthly_rent_min = findViewById(R.id.monthly_rent_editText_min);
        CheckBox checkBox = findViewById(R.id.monthly_rent_checkBox);

        minStr = monthly_rent_max.getText().toString();
        maxStr = monthly_rent_min.getText().toString();

        minValue = minStr.equals("") ? 0 : Integer.parseInt(minStr);
        maxValue = maxStr.equals("") ? Integer.MAX_VALUE : Integer.parseInt(maxStr);
        boolean isContain = checkBox.isChecked();
        Filter.setMonthlyRen(isContain, minValue, maxValue);

        // 나머지 사항들
        for (ToggleButtonGroup group : toggleButtonGroups) {
            if (group != null) {
                group.saveCheckedStates();
                HashMap<String, Boolean> list = group.getToggleButtonCheckedStates();
                Filter.addCheckedStates(group.getTitle(), list);
            }
        }
    }

    private void resetFilter() {
        // 보증금
        TextView deposit_textView = findViewById(R.id.deposit_textView);
        EditText deposit_max = findViewById(R.id.deposit_editText_max);
        EditText deposit_min = findViewById(R.id.deposit_editText_min);

        deposit_textView.setText("0원 ~ 전체");
        deposit_max.setText("");
        deposit_min.setText("");
        Filter.setDeposit(0, Integer.MAX_VALUE);

        // 월세금
        TextView monthly_rent_textView = findViewById(R.id.monthly_rent_textView);
        EditText monthly_rent_max = findViewById(R.id.monthly_rent_editText_max);
        EditText monthly_rent_min = findViewById(R.id.monthly_rent_editText_min);
        CheckBox checkBox = findViewById(R.id.monthly_rent_checkBox);

        monthly_rent_textView.setText("0원 ~ 전체");
        monthly_rent_max.setText("");
        monthly_rent_min.setText("");
        checkBox.setChecked(false);
        Filter.setMonthlyRen(false, 0, Integer.MAX_VALUE);

        // 나머지 사항들
        for (ToggleButtonGroup group : toggleButtonGroups) {
            if (group != null) {
                group.resetCheckedStates();
                HashMap<String, Boolean> list = group.getToggleButtonCheckedStates();
                Filter.addCheckedStates(group.getTitle(), list);
            }
        }
    }
}