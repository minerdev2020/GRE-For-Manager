package com.minerdev.greformanager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.minerdev.greformanager.databinding.FragmentInfo2Binding;

public class InfoFragment2 extends Fragment implements OnSaveDataListener {
    private String houseType;

    private FragmentInfo2Binding binding;
    private HouseModifyViewModel viewModel;
    private HouseParcelableData house;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info2, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(HouseModifyViewModel.class);


        // 스피너 초기화
        setSpinners();


        // 체크박스 초기화
        setCheckBoxes();


        // 전용 면적, 임대 면적 초기화
        setAreaEditTexts();


        // 준공년월 초기화
        binding.houseModify2ImageButtonBuiltDate.setOnClickListener(
                v -> showDatePickerDialog((v1, result) -> binding.houseModify2TextViewBuiltDate.setText(result))
        );


        // 입주가능일 초기화
        binding.houseModify2ImageButtonMoveDate.setEnabled(false);
        binding.houseModify2ImageButtonMoveDate.setOnClickListener(
                v -> showDatePickerDialog((v1, result) -> binding.houseModify2TextViewMoveDate.setText(result))
        );

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public boolean checkData() {
        if (binding.houseModify2EditTextAreaMeter.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModify2EditTextRentAreaMeter.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModify2EditTextBuildingFloor.getText().toString().equals("")) {
            return false;
        }

        if (!binding.houseModify2CheckBoxUnderground.isChecked()
                && binding.houseModify2EditTextFloor.getText().toString().equals("")) {
            return false;
        }

        if ((houseType.equals("주택") || houseType.equals("오피스텔"))
                && binding.houseModify2SpinnerStructure.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModify2SpinnerBathroom.getSelectedItemId() == 0) {
            return false;
        }

        if ((houseType.equals("사무실") || houseType.equals("상가, 점포"))
                && binding.houseModify2RadioGroup.getCheckedRadioButtonId() == -1) {
            return false;
        }

        if (binding.houseModify2SpinnerDirection.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModify2TextViewBuiltDate.getText().toString().equals("")) {
            return false;
        }

        if (!binding.houseModify2CheckBoxMoveNow.isChecked()
                && binding.houseModify2TextViewMoveDate.getText().toString().equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public void saveData() {
        house.area_meter = parseFloat(binding.houseModify2EditTextAreaMeter.getText().toString());
        house.rent_area_meter = parseFloat(binding.houseModify2EditTextRentAreaMeter.getText().toString());
        house.building_floor = (byte) parseInt(binding.houseModify2EditTextBuildingFloor.getText().toString());

        if (binding.houseModify2CheckBoxUnderground.isChecked()) {
            house.floor = -1;

        } else {
            house.floor = Byte.parseByte(binding.houseModify2EditTextFloor.getText().toString());
        }

        house.structure = (byte) binding.houseModify2SpinnerStructure.getSelectedItemPosition();
        house.bathroom = (byte) binding.houseModify2SpinnerBathroom.getSelectedItemPosition();

        if (binding.houseModify2RadioButtonOutside.isChecked()) {
            house.bathroom_location = 0;

        } else {
            house.bathroom_location = 1;
        }

        house.direction = (byte) binding.houseModify2SpinnerDirection.getSelectedItemId();
        house.built_date = binding.houseModify2TextViewBuiltDate.getText().toString();
        house.move_date = binding.houseModify2TextViewMoveDate.getText().toString();
    }

    public void initData() {
        house = viewModel.getHouse();
        houseType = Constants.getInstance().HOUSE_TYPE.get(house.house_type);

        // 구조 초기화 ('매물 종류'가 '주택'이나 '오피스텔'일때만 '구조' 항목이 보임)
        int visibility = houseType.equals("주택") || houseType.equals("오피스텔") ? View.VISIBLE : View.GONE;
        binding.houseModify2TableRowStructure.setVisibility(visibility);
        binding.houseModify2View.setVisibility(visibility);
        binding.houseModify2SpinnerStructure.setSelection(0);


        // 화장실 위치 초기화 ('매물 종류'가 '사무실'이나 '상가, 점포'일때만 '화장실 위치' 항목이 보임)
        binding.houseModify2TableRowLocation.setVisibility(houseType.equals("사무실") || houseType.equals("상가, 점포") ? View.VISIBLE : View.GONE);
        binding.houseModify2RadioGroup.clearCheck();

        // 데이터 읽기
        if (house != null) {
            loadData();
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadData() {
        binding.houseModify2EditTextAreaMeter.setText(house.area_meter == 0 ? "" : String.valueOf(house.area_meter));
        binding.houseModify2EditTextRentAreaMeter.setText(house.rent_area_meter == 0 ? "" : String.valueOf(house.rent_area_meter));

        binding.houseModify2EditTextAreaPyeong.setText(house.area_meter == 0 ? "" : String.format("%.2f", house.area_meter * Constants.getInstance().METER_TO_PYEONG));
        binding.houseModify2EditTextRentAreaPyeong.setText(house.rent_area_meter == 0 ? "" : String.format("%.2f", house.rent_area_meter * Constants.getInstance().METER_TO_PYEONG));

        binding.houseModify2EditTextBuildingFloor.setText(house.building_floor == 0 ? "" : String.valueOf(house.building_floor));

        if (house.floor == -1) {
            binding.houseModify2CheckBoxUnderground.setChecked(true);

        } else {
            binding.houseModify2EditTextFloor.setText(house.floor == 0 ? "" : String.valueOf(house.floor));
        }

        binding.houseModify2SpinnerStructure.setSelection(house.structure);
        binding.houseModify2SpinnerBathroom.setSelection(house.bathroom);

        if (house.bathroom_location == 0) {
            binding.houseModify2RadioButtonOutside.setChecked(true);

        } else {
            binding.houseModify2RadioButtonInside.setChecked(true);
        }

        binding.houseModify2SpinnerDirection.setSelection(house.direction);
        binding.houseModify2TextViewBuiltDate.setText(house.built_date);

        if (house.move_date == null || house.move_date.equals("")) {
            binding.houseModify2CheckBoxMoveNow.setChecked(true);

        } else {
            binding.houseModify2TextViewMoveDate.setText(house.move_date);
        }
    }

    private void setSpinners() {
        // 구조 초기화
        ArrayAdapter<String> arrayAdapterStructure = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterStructure.addAll(getResources().getStringArray(R.array.structure));

        binding.houseModify2SpinnerStructure.setAdapter(arrayAdapterStructure);


        // 욕실 갯수 초기화
        ArrayAdapter<String> arrayAdapterBathroom = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterBathroom.addAll(getResources().getStringArray(R.array.bathroom));

        binding.houseModify2SpinnerBathroom.setAdapter(arrayAdapterBathroom);


        // 방향 초기화
        ArrayAdapter<String> arrayAdapterDirection = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterDirection.addAll(getResources().getStringArray(R.array.direction));

        binding.houseModify2SpinnerDirection.setAdapter(arrayAdapterDirection);
    }

    private void setCheckBoxes() {
        // 층 초기화
        binding.houseModify2CheckBoxUnderground.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModify2EditTextFloor.setText("");
            binding.houseModify2EditTextFloor.setEnabled(!isChecked);
        });


        // 입주 가능일 초기화
        binding.houseModify2CheckBoxMoveNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModify2TextViewMoveDate.setText("");
            binding.houseModify2ImageButtonMoveDate.setEnabled(!isChecked);
        });
    }

    @SuppressLint("DefaultLocale")
    private void setAreaEditTexts() {
        // 전용 면적 초기화
        binding.houseModify2EditTextAreaPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModify2EditTextAreaPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModify2EditTextAreaMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModify2EditTextAreaMeter.setText(String.format("%.2f",
                                area * Constants.getInstance().PYEONG_TO_METER));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.houseModify2EditTextAreaMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModify2EditTextAreaMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModify2EditTextAreaPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModify2EditTextAreaPyeong.setText(String.format("%.2f",
                                area * Constants.getInstance().METER_TO_PYEONG));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // 임대 면적 초기화
        binding.houseModify2EditTextRentAreaPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModify2EditTextRentAreaPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModify2EditTextRentAreaMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModify2EditTextRentAreaMeter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.houseModify2EditTextRentAreaMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModify2EditTextRentAreaMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModify2EditTextRentAreaPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModify2EditTextRentAreaPyeong.setText(String.format("%.2f", area * 0.3025));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showDatePickerDialog(OnClickListener listener) {
        Dialog datePickerDialog = new Dialog(getContext());
        datePickerDialog.setContentView(R.layout.dialog_date_picker);

        Button buttonBack = datePickerDialog.findViewById(R.id.date_picker_dialog_button_back);
        buttonBack.setOnClickListener(v -> datePickerDialog.dismiss());

        Button buttonSelect = datePickerDialog.findViewById(R.id.date_picker_dialog_button_select);
        buttonSelect.setOnClickListener(v -> {
            DatePicker datePicker = datePickerDialog.findViewById(R.id.date_picker_dialog_datePicker);
            String date = datePicker.getYear() + "." + datePicker.getMonth() + "." + datePicker.getDayOfMonth();
            if (listener != null) {
                listener.onClick(v, date);
            }

            datePickerDialog.dismiss();
        });

        datePickerDialog.show();
    }

    private int parseInt(String number) {
        if (number == null || number.equals("")) {
            return 0;

        } else {
            return Integer.parseInt(number);
        }
    }

    private float parseFloat(String number) {
        if (number == null || number.equals("")) {
            return 0;

        } else {
            return Float.parseFloat(number);
        }
    }

    public interface OnClickListener {
        void onClick(View v, String result);
    }
}