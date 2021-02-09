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

    public String houseType;
    public String paymentType;

    ArrayAdapter<String> arrayAdapterPayment;
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


        // 스피너 초기화
        setSpinners();


        // 체크박스 초기화
        setCheckBoxes();


        // 전용 면적, 임대 면적 초기화
        setAreaEditTexts();


        // 준공년월 초기화
        binding.houseModifyImageButtonBuiltDate.setOnClickListener(
                v -> showDatePickerDialog((v1, result) -> binding.houseModifyTextViewBuiltDate.setText(result))
        );


        binding.houseModifyImageButtonMoveDate.setOnClickListener(
                v -> showDatePickerDialog((v1, result) -> binding.houseModifyTextViewMoveDate.setText(result))
        );


        // 관리비 초기화
        toggleButtonGroupManageFeeContains = new ToggleButtonGroup(getContext(), "관리비 항목");
        toggleButtonGroupManageFeeContains.addToggleButtons(getResources().getStringArray(R.array.manage_fee));

        for (ToggleButton toggleButton : toggleButtonGroupManageFeeContains.getToggleButtons()) {
            binding.houseModifyFlowLayoutManageFee.addView(toggleButton);
        }


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

        if (!(paymentType.equals("월세") || paymentType.equals("단기임대") || paymentType.equals("임대"))
                && binding.houseModifyEditTextPrice.getText().toString().equals("")) {
            return false;

        } else if (paymentType.equals("월세") || paymentType.equals("단기임대") || paymentType.equals("임대")
                && (binding.houseModifyEditTextDeposit.getText().toString().equals("")
                || binding.houseModifyEditTextMonthlyRent.getText().toString().equals(""))) {
            return false;
        }

        if (houseType.equals("상가, 점포") && paymentType.equals("임대")
                && binding.houseModifyEditTextPremium.getText().toString().equals("")) {
            return false;
        }

        if (!binding.houseModifyCheckBoxManageFee.isChecked()
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

        if (!binding.houseModifyCheckBoxUnderground.isChecked()
                && binding.houseModifyEditTextFloor.getText().toString().equals("")) {
            return false;
        }

        if ((houseType.equals("주택") || houseType.equals("오피스텔"))
                && binding.houseModifySpinnerStructure.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModifySpinnerBathroom.getSelectedItemId() == 0) {
            return false;
        }

        if ((houseType.equals("사무실") || houseType.equals("상가, 점포"))
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
        House.ParcelableData data = new House.ParcelableData();

        data.address = binding.houseModifyTextViewAddress.getText().toString();
        data.house_number = binding.houseModifyEditTextNumber.getText().toString();
        data.house_type = (byte) binding.houseModifySpinnerHouseType.getSelectedItemPosition();
        data.facility = (byte) (binding.houseModifyCheckBoxFacility.isChecked() ? 1 : 0);
        data.payment_type = (byte) binding.houseModifySpinnerPaymentType.getSelectedItemPosition();
        data.price = parseInt(binding.houseModifyEditTextPrice.getText().toString());
        data.deposit = parseInt(binding.houseModifyEditTextDeposit.getText().toString());
        data.monthly_rent = parseInt(binding.houseModifyEditTextMonthlyRent.getText().toString());
        data.premium = parseInt(binding.houseModifyEditTextPremium.getText().toString());
        data.manage_fee = parseInt(binding.houseModifyEditTextManageFee.getText().toString());
        data.manage_fee_contains = toggleButtonGroupManageFeeContains.getCheckedToggleButtonTextsInSingleLine();
        data.area_meter = parseFloat(binding.houseModifyEditTextAreaMeter.getText().toString());
        data.rent_area_meter = parseFloat(binding.houseModifyEditTextRentAreaMeter.getText().toString());
        data.building_floor = (byte) parseInt(binding.houseModifyEditTextBuildingFloor.getText().toString());

        if (binding.houseModifyCheckBoxUnderground.isChecked()) {
            data.floor = -1;

        } else {
            data.floor = Byte.parseByte(binding.houseModifyEditTextFloor.getText().toString());
        }

        data.structure = (byte) binding.houseModifySpinnerStructure.getSelectedItemPosition();
        data.bathroom = (byte) binding.houseModifySpinnerBathroom.getSelectedItemPosition();
        data.bathroom_location = (byte) binding.houseModifyRadioGroup.getCheckedRadioButtonId();
        data.direction = (byte) binding.houseModifySpinnerDirection.getSelectedItemId();
        data.built_date = binding.houseModifyTextViewBuiltDate.getText().toString();
        data.move_date = binding.houseModifyTextViewMoveDate.getText().toString();
        data.options = toggleButtonGroupOptions.getCheckedToggleButtonTextsInSingleLine();
        data.detail_info = binding.houseModifyDetailInfo.getText().toString();
        data.phone = binding.houseModifyEditTextPhone.getText().toString();

        SendData.getInstance().house = data;
    }

    void setSpinners() {
        // 계약 형태 초기화
        arrayAdapterPayment = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);

        binding.houseModifySpinnerPaymentType.setAdapter(arrayAdapterPayment);
        binding.houseModifySpinnerPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onPaymentTypeItemSelected(parent, view, position, id);
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
                onHouseTypeItemSelected(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                arrayAdapterPayment.clear();
            }
        });

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
    }

    void setCheckBoxes() {
        // 관리비 초기화
        binding.houseModifyCheckBoxManageFee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModifyEditTextManageFee.setText("");
            binding.houseModifyEditTextManageFee.setEnabled(!isChecked);
            binding.houseModifyFlowLayoutManageFee.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });


        // 층 초기화
        binding.houseModifyCheckBoxUnderground.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModifyEditTextFloor.setText("");
            binding.houseModifyEditTextFloor.setEnabled(!isChecked);
        });


        // 입주 가능일 초기화
        binding.houseModifyCheckBoxMoveNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModifyTextViewMoveDate.setText("");
            binding.houseModifyImageButtonMoveDate.setEnabled(!isChecked);
        });
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
                        binding.houseModifyEditTextAreaMeter.setText(String.format("%.2f",
                                area * Constants.getInstance().PYEONG_TO_METER));
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
                        binding.houseModifyEditTextAreaPyeong.setText(String.format("%.2f",
                                area * Constants.getInstance().METER_TO_PYEONG));
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

    void onPaymentTypeItemSelected(AdapterView<?> parent, View view, int position, long id) {
        paymentType = binding.houseModifySpinnerPaymentType.getSelectedItem().toString();

        binding.houseModifyEditTextPrice.setText("");
        binding.houseModifyEditTextDeposit.setText("");
        binding.houseModifyEditTextMonthlyRent.setText("");
        binding.houseModifyEditTextPremium.setText("");

        if (paymentType.equals("월세") || paymentType.equals("단기임대") || paymentType.equals("임대")) {
            binding.houseModifyTableRowPrice.setVisibility(View.GONE);
            binding.houseModifyTableRowDeposit.setVisibility(View.VISIBLE);
            binding.houseModifyTableRowMonthlyRent.setVisibility(View.VISIBLE);

        } else {
            binding.houseModifyTableRowPrice.setVisibility(View.VISIBLE);
            binding.houseModifyTableRowDeposit.setVisibility(View.GONE);
            binding.houseModifyTableRowMonthlyRent.setVisibility(View.GONE);
        }

        if (houseType.equals("상가, 점포") && paymentType.equals("임대")) {
            binding.houseModifyTableRowPremium.setVisibility(View.VISIBLE);

        } else {
            binding.houseModifyTableRowPremium.setVisibility(View.GONE);
        }
    }

    void onHouseTypeItemSelected(AdapterView<?> parent, View view, int position, long id) {
        houseType = binding.houseModifySpinnerHouseType.getSelectedItem().toString();

        if (position > 0) {
            arrayAdapterPayment.clear();
            arrayAdapterPayment.addAll(Constants.getInstance().PAYMENT_TYPE.get(position - 1));
            binding.houseModifySpinnerPaymentType.setSelection(0);

        } else {
            arrayAdapterPayment.clear();
        }


        // 시설 유무 초기화
        binding.houseModifyCheckBoxFacility.setEnabled(houseType.equals("상가, 점포"));
        binding.houseModifyCheckBoxFacility.setChecked(false);


        // 구조 초기화 ('매물 종류'가 '주택'이나 '오피스텔'일때만 '구조' 항목이 보임)
        int visibility = houseType.equals("주택") || houseType.equals("오피스텔") ? View.VISIBLE : View.GONE;
        binding.houseModifyTableRowStructure.setVisibility(visibility);
        binding.houseModifyView.setVisibility(visibility);
        binding.houseModifySpinnerStructure.setSelection(0);


        // 화장실 위치 초기화 ('매물 종류'가 '사무실'이나 '상가, 점포'일때만 '화장실 위치' 항목이 보임)
        binding.houseModifyTableRowLocation.setVisibility(houseType.equals("사무실") || houseType.equals("상가, 점포") ? View.VISIBLE : View.GONE);
        binding.houseModifyRadioGroup.clearCheck();
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