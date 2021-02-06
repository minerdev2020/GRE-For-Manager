package com.minerdev.greformanager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ToggleButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.minerdev.greformanager.databinding.FragmentInfoBinding;

public class InfoFragment extends Fragment implements OnSaveDataListener {
    final Handler handler = new Handler();

    ToggleButtonGroup toggleButtonGroupManageFeeContains;
    ToggleButtonGroup toggleButtonGroupOptions;
    Dialog addressDialog;

    FragmentInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false);


        // 주소 입력 초기화
        binding.houseModifyMaterialButtonSearch.setOnClickListener(v -> showAddressDialog());


        // 계약 형태 초기화
        ArrayAdapter<String> arrayAdapterPayment = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);

        binding.houseModifySpinnerPaymentType.setAdapter(arrayAdapterPayment);
        binding.houseModifySpinnerPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 가격, 보증금, 월세 초기화
                // ('계약 형태'가 '월세', '단기임대' 혹은 '임대'일때만 '보증금'와 '월세' 항목이 보임,
                // 그 외의 상황에는 '가격' 항목이 보임)
                String value = binding.houseModifySpinnerPaymentType.getSelectedItem().toString();
                if (value.equals("월세") || value.equals("단기임대") || value.equals("임대")) {
                    binding.houseModifyTableRowPrice.setVisibility(View.GONE);
                    binding.houseModifyTableRowDeposit.setVisibility(View.VISIBLE);
                    binding.houseModifyTableRowMonthlyRent.setVisibility(View.VISIBLE);

                } else {
                    binding.houseModifyTableRowPrice.setVisibility(View.VISIBLE);
                    binding.houseModifyTableRowDeposit.setVisibility(View.GONE);
                    binding.houseModifyTableRowMonthlyRent.setVisibility(View.GONE);
                }

                binding.houseModifyEditTextPrice.setText("");
                binding.houseModifyEditTextDeposit.setText("");
                binding.houseModifyEditTextMonthlyRent.setText("");


                // 권리금 초기화 ('매물 종류'가 '상가, 점포'이면서 '계약 형태'가 '임대'일때만 '권리금' 항목이 보임)
                String houseValue = binding.houseModifySpinnerHouseType.getSelectedItem().toString();
                binding.houseModifyTableRowPremium.setVisibility(houseValue.equals("상가, 점포") && value.equals("임대") ? View.VISIBLE : View.GONE);

                binding.houseModifyEditTextPremium.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 매물 종류 초기화
        ArrayAdapter<String> arrayAdapterHouse = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterHouse.addAll(getResources().getStringArray(R.array.houseType));

        binding.houseModifySpinnerHouseType.setAdapter(arrayAdapterHouse);
        binding.houseModifySpinnerHouseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 계약 종류 초기화
                if (position > 0) {
                    arrayAdapterPayment.clear();
                    String temp = getResources().getStringArray(R.array.paymentType)[position - 1];
                    arrayAdapterPayment.addAll(temp.split(" "));
                    binding.houseModifySpinnerPaymentType.setSelection(0);

                } else {
                    arrayAdapterPayment.clear();
                }


                // 시설 유무 초기화
                String value = binding.houseModifySpinnerHouseType.getSelectedItem().toString();
                binding.houseModifyCheckBoxFacility.setEnabled(value.equals("상가, 점포"));
                binding.houseModifyCheckBoxFacility.setChecked(false);


                // 구조 초기화 ('매물 종류'가 '주택'이나 '오피스텔'일때만 '구조' 항목이 보임)
                final int visibility = value.equals("주택") || value.equals("오피스텔") ? View.VISIBLE : View.GONE;
                binding.houseModifyTableRowStructure.setVisibility(visibility);

                binding.houseModifyView.setVisibility(visibility);

                binding.houseModifySpinnerStructure.setSelection(0);


                // 화장실 위치 초기화 ('매물 종류'가 '사무실'이나 '상가, 점포'일때만 '화장실 위치' 항목이 보임)
                binding.houseModifyTableRowLocation.setVisibility(value.equals("사무실") || value.equals("상가, 점포") ? View.VISIBLE : View.GONE);
                binding.houseModifyRadioGroup.clearCheck();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                arrayAdapterPayment.clear();
            }
        });


        // 관리비 초기화
        binding.houseModifyCheckBoxManageFee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModifyEditTextManageFee.setEnabled(!isChecked);
            binding.houseModifyEditTextManageFee.setText("");

            binding.houseModifyFlowLayoutManageFee.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });
        binding.houseModifyCheckBoxManageFee.setChecked(true);

        toggleButtonGroupManageFeeContains = new ToggleButtonGroup(getContext(), "관리비 항목");
        toggleButtonGroupManageFeeContains.addToggleButtons(getResources().getStringArray(R.array.manage_fee));

        for (ToggleButton toggleButton : toggleButtonGroupManageFeeContains.getToggleButtons()) {
            binding.houseModifyFlowLayoutManageFee.addView(toggleButton);
        }


        // 전용 면적, 임대 면적 초기화
        setAreaEditTexts();


        // 층 초기화
        binding.houseModifyCheckBoxUnderground.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModifyEditTextFloor.setEnabled(!isChecked);
            binding.houseModifyEditTextFloor.setText("");
        });
        binding.houseModifyCheckBoxUnderground.setChecked(false);


        // 구조 초기화
        ArrayAdapter<String> arrayAdapterStructure = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterStructure.addAll(getResources().getStringArray(R.array.structure));

        binding.houseModifySpinnerStructure.setAdapter(arrayAdapterStructure);


        // 욕실 갯수 초기화
        ArrayAdapter<String> arrayAdapterBathroom = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterBathroom.addAll(getResources().getStringArray(R.array.bathroom));

        binding.houseModifySpinnerBathroom.setAdapter(arrayAdapterBathroom);


        // 방향 초기화
        ArrayAdapter<String> arrayAdapterDirection = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterDirection.addAll(getResources().getStringArray(R.array.direction));

        binding.houseModifySpinnerDirection.setAdapter(arrayAdapterDirection);


        // 준공년월 초기화
        binding.houseModifyImageButtonBuiltDate.setOnClickListener(
                v -> showDatePickerDialog(
                        (v1, result) -> binding.houseModifyTextViewBuiltDate.setText(result)
                )
        );


        // 입주 가능일 초기화
        binding.houseModifyImageButtonMoveDate.setOnClickListener(
                v -> showDatePickerDialog(
                        (v12, result) -> binding.houseModifyTextViewMoveDate.setText(result)
                )
        );

        binding.houseModifyCheckBoxMoveNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModifyTextViewMoveDate.setEnabled(!isChecked);
            binding.houseModifyTextViewMoveDate.setText("");

            binding.houseModifyImageButtonMoveDate.setEnabled(!isChecked);
        });
        binding.houseModifyCheckBoxMoveNow.setChecked(false);


        // 옵션 정보 초기화
        toggleButtonGroupOptions = new ToggleButtonGroup(getContext(), "옵션 항목");
        toggleButtonGroupOptions.addToggleButtons(getResources().getStringArray(R.array.option));
        for (ToggleButton toggleButton : toggleButtonGroupOptions.getToggleButtons()) {
            binding.houseModifyFlowLayoutOption.addView(toggleButton);
        }


        // 담당자 정보 초기화
        binding.houseModifyEditTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        return binding.getRoot();
    }

    @Override
    public boolean checkData() {
        if (binding.houseModifyTextViewAddress.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyEditTextNumber.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifySpinnerPaymentType.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModifySpinnerHouseType.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModifyTableRowPrice.getVisibility() == View.VISIBLE
                && binding.houseModifyEditTextPrice.getText().toString().equals("")) {
            return false;

        } else if (binding.houseModifyEditTextPrice.getVisibility() == View.GONE
                && (binding.houseModifyEditTextDeposit.getText().toString().equals("")
                || binding.houseModifyEditTextMonthlyRent.getText().toString().equals(""))) {
            return false;
        }

        if (binding.houseModifyTableRowPremium.getVisibility() == View.VISIBLE
                && binding.houseModifyEditTextPremium.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyCheckBoxManageFee.isChecked()
                && binding.houseModifyEditTextManageFee.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyEditTextAreaMeter.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyEditTextRentAreaMeter.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyEditTextBuildingFloor.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyEditTextFloor.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyTableRowStructure.getVisibility() == View.VISIBLE
                && binding.houseModifySpinnerStructure.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModifySpinnerBathroom.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModifyTableRowLocation.getVisibility() == View.VISIBLE
                && binding.houseModifyRadioGroup.getCheckedRadioButtonId() == -1) {
            return false;
        }

        if (binding.houseModifySpinnerDirection.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModifyTextViewBuiltDate.getText().toString().equals("")) {
            return false;
        }

        if (!binding.houseModifyCheckBoxMoveNow.isChecked()
                && binding.houseModifyTextViewMoveDate.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModifyEditTextPhone.getText().toString().equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public void saveData() {
        House.SerializedData data = new House.SerializedData();

        data.address = binding.houseModifyTextViewAddress.getText().toString();
        data.houseNumber = binding.houseModifyEditTextNumber.getText().toString();
        data.paymentType = (byte) binding.houseModifySpinnerPaymentType.getSelectedItemPosition();
        data.houseType = (byte) binding.houseModifySpinnerHouseType.getSelectedItemPosition();
        data.price = parseInt(binding.houseModifyEditTextPrice.getText().toString());
        data.deposit = parseInt(binding.houseModifyEditTextDeposit.getText().toString());
        data.monthlyRent = parseInt(binding.houseModifyEditTextMonthlyRent.getText().toString());
        data.premium = parseInt(binding.houseModifyEditTextPremium.getText().toString());
        data.manageFee = parseInt(binding.houseModifyEditTextManageFee.getText().toString());
        data.manageFeeContains = toggleButtonGroupManageFeeContains.getCheckedToggleButtonTextsInSingleLine();
        data.areaMeter = parseFloat(binding.houseModifyEditTextAreaMeter.getText().toString());
        data.rentAreaMeter = parseFloat(binding.houseModifyEditTextRentAreaMeter.getText().toString());
        data.buildingFloor = (byte) parseInt(binding.houseModifyEditTextBuildingFloor.getText().toString());

        if (binding.houseModifyCheckBoxUnderground.isChecked()) {
            data.floor = -1;

        } else {
            data.floor = Byte.parseByte(binding.houseModifyEditTextFloor.getText().toString());
        }

        data.structure = (byte) binding.houseModifySpinnerStructure.getSelectedItemPosition();
        data.bathroom = (byte) binding.houseModifySpinnerBathroom.getSelectedItemPosition();
        data.bathroomLocation = (byte) binding.houseModifyRadioGroup.getCheckedRadioButtonId();
        data.direction = (byte) binding.houseModifySpinnerDirection.getSelectedItemId();
        data.builtDate = binding.houseModifyTextViewBuiltDate.getText().toString();
        data.moveDate = binding.houseModifyTextViewMoveDate.getText().toString();
        data.options = toggleButtonGroupOptions.getCheckedToggleButtonTextsInSingleLine();
        data.detailInfo = binding.houseModifyDetailInfo.getText().toString();
        data.phone = binding.houseModifyEditTextPhone.getText().toString();

        SendData.getInstance().house = data;
    }

    @SuppressLint("DefaultLocale")
    void setAreaEditTexts() {
        // 전용 면적 초기화
        binding.houseModifyEditTextAreaPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModifyEditTextAreaPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModifyEditTextAreaMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModifyEditTextAreaMeter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.houseModifyEditTextAreaMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModifyEditTextAreaMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModifyEditTextAreaPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModifyEditTextAreaPyeong.setText(String.format("%.2f", area * 0.3025));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // 임대 면적 초기화
        binding.houseModifyEditTextRentAreaPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModifyEditTextRentAreaPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModifyEditTextRentAreaMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModifyEditTextRentAreaMeter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.houseModifyEditTextRentAreaMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.houseModifyEditTextRentAreaMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        binding.houseModifyEditTextRentAreaPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        binding.houseModifyEditTextRentAreaPyeong.setText(String.format("%.2f", area * 0.3025));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    void showAddressDialog() {
        addressDialog = new Dialog(getContext());
        addressDialog.setContentView(R.layout.dialog_address);

        WebView webView = addressDialog.findViewById(R.id.address_dialog_button_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "GREApp");
        webView.loadUrl(getString(R.string.web_server_dns) + "/get_daum_address.php");

        WindowManager.LayoutParams params = addressDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        addressDialog.getWindow().setAttributes(params);

        Button buttonBack = addressDialog.findViewById(R.id.address_dialog_button_back);
        buttonBack.setOnClickListener(v -> addressDialog.dismiss());

        addressDialog.show();
    }

    void showDatePickerDialog(OnClickListener listener) {
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

    int parseInt(String number) {
        if (number == null || number.equals("")) {
            return 0;

        } else {
            return Integer.parseInt(number);
        }
    }

    float parseFloat(String number) {
        if (number == null || number.equals("")) {
            return 0;

        } else {
            return Float.parseFloat(number);
        }
    }

    public interface OnClickListener {
        void onClick(View v, String result);
    }

    class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(() -> {
                binding.houseModifyTextViewAddress.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                addressDialog.dismiss();
            });
        }
    }
}