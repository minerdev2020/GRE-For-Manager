package com.minerdev.greformanager;

import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.minerdev.greformanager.databinding.FragmentInfo3Binding;

public class InfoFragment3 extends Fragment implements OnSaveDataListener {
    private ToggleButtonGroup toggleButtonGroupOptions;

    private FragmentInfo3Binding binding;
    private HouseModifyViewModel viewModel;
    private House house;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info3, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(HouseModifyViewModel.class);


        // 옵션 정보 초기화
        toggleButtonGroupOptions = new ToggleButtonGroup(getContext(), "옵션 항목");
        toggleButtonGroupOptions.addToggleButtons(getResources().getStringArray(R.array.option));
        for (ToggleButton toggleButton : toggleButtonGroupOptions.getToggleButtons()) {
            binding.houseModify3FlowLayoutOption.addView(toggleButton);
        }


        // 담당자 정보 초기화
        binding.houseModify3EditTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
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
        House data = house;

        data.options = toggleButtonGroupOptions.getCheckedToggleButtonTextsInSingleLine();
        data.detail_info = binding.houseModify3DetailInfo.getText().toString();
        data.phone = binding.houseModify3EditTextPhone.getText().toString();
    }

    public void initData() {
        // 데이터 읽기
        house = viewModel.getHouse();
        if (house != null) {
            loadData();
        }
    }

    private void loadData() {
        if (house.options != null && !house.options.equals("")) {
            String[] optionsTexts = house.options.split("\\|");
            for (String text : optionsTexts) {
                toggleButtonGroupOptions.setToggleButtonCheckedState(text, true);
            }
        }

        binding.houseModify3DetailInfo.setText(house.detail_info);
        binding.houseModify3EditTextPhone.setText(house.phone);
    }
}