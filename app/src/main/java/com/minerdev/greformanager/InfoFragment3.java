package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.minerdev.greformanager.databinding.FragmentInfo3Binding;

public class InfoFragment3 extends Fragment implements OnSaveDataListener {
    private ToggleButtonGroup toggleButtonGroupOptions;

    private FragmentInfo3Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info3, container, false);


        // 옵션 정보 초기화
        toggleButtonGroupOptions = new ToggleButtonGroup(getContext(), "옵션 항목");
        toggleButtonGroupOptions.addToggleButtons(getResources().getStringArray(R.array.option));
        for (ToggleButton toggleButton : toggleButtonGroupOptions.getToggleButtons()) {
            binding.houseModify3FlowLayoutOption.addView(toggleButton);
        }


        // 담당자 정보 초기화
        binding.houseModify3EditTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


        // 데이터 읽기
        Intent intent = getActivity().getIntent();
        String mode = intent.getStringExtra("mode");
        if (mode.equals("modify")) {
            House.ParcelableData data = intent.getParcelableExtra("house_value");
            readData(data);
        }

        return binding.getRoot();
    }

    @Override
    public boolean checkData() {
        if (binding.houseModify3EditTextPhone.getText().toString().equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public void saveData() {
        House.ParcelableData data = SendData.getInstance().house;

        data.options = toggleButtonGroupOptions.getCheckedToggleButtonTextsInSingleLine();
        data.detail_info = binding.houseModify3DetailInfo.getText().toString();
        data.phone = binding.houseModify3EditTextPhone.getText().toString();
    }

    private void readData(House.ParcelableData data) {
        String[] optionsTexts = data.options.split("\\|");
        for (String text : optionsTexts) {
            toggleButtonGroupOptions.setToggleButtonCheckedState(text, true);
        }

        binding.houseModify3DetailInfo.setText(data.detail_info);
        binding.houseModify3EditTextPhone.setText(data.phone);
    }
}