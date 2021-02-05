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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.FlowLayout;

public class InfoFragment extends Fragment implements OnSaveDataListener {
    private final Handler handler = new Handler();
    private ViewGroup rootView;
    private ToggleButtonGroup toggleButtonGroupManageFeeContains;
    private ToggleButtonGroup toggleButtonGroupOptions;
    private Dialog addressDialog;

    private CheckBox checkBoxFacility;
    private CheckBox checkBoxManageFee;
    private CheckBox checkBoxMoveNow;
    private CheckBox checkBoxUnderground;

    private EditText editTextAreaMeter;
    private EditText editTextAreaPyeong;
    private EditText editTextBuildingFloor;
    private EditText editTextDeposit;
    private EditText editTextFloor;
    private EditText editTextNumber;
    private EditText editTextManageFee;
    private EditText editTextMonthlyRent;
    private EditText editTextPhone;
    private EditText editTextPrice;
    private EditText editTextPremium;
    private EditText editTextRentAreaPyeong;
    private EditText editTextRentAreaMeter;

    private RadioGroup radioGroupLocation;

    private Spinner spinnerBathroom;
    private Spinner spinnerDirection;
    private Spinner spinnerHouse;
    private Spinner spinnerPayment;
    private Spinner spinnerStructure;

    private TextView textViewAddress;
    private TextView textViewBuiltDate;
    private TextView textViewMoveDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);


        // 뷰 불러오기
        setViews();


        // 주소 입력 초기화
        MaterialButton buttonSearchAddress = rootView.findViewById(R.id.house_modify_materialButton_search);
        buttonSearchAddress.setOnClickListener(v -> showAddressDialog());


        // 계약 형태 초기화
        ArrayAdapter<String> arrayAdapterPayment = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);

        spinnerPayment.setAdapter(arrayAdapterPayment);
        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 가격, 보증금, 월세 초기화
                // ('계약 형태'가 '월세', '단기임대' 혹은 '임대'일때만 '보증금'와 '월세' 항목이 보임,
                // 그 외의 상황에는 '가격' 항목이 보임)
                TableRow tableRowPrice = rootView.findViewById(R.id.house_modify_tableRow_price);
                TableRow tableRowDeposit = rootView.findViewById(R.id.house_modify_tableRow_deposit);
                TableRow tableRowMonthlyRent = rootView.findViewById(R.id.house_modify_tableRow_monthly_rent);

                String value = spinnerPayment.getSelectedItem().toString();
                if (value.equals("월세") || value.equals("단기임대") || value.equals("임대")) {
                    tableRowPrice.setVisibility(View.GONE);
                    tableRowDeposit.setVisibility(View.VISIBLE);
                    tableRowMonthlyRent.setVisibility(View.VISIBLE);

                } else {
                    tableRowPrice.setVisibility(View.VISIBLE);
                    tableRowDeposit.setVisibility(View.GONE);
                    tableRowMonthlyRent.setVisibility(View.GONE);
                }

                editTextPrice.setText("");
                editTextDeposit.setText("");
                editTextMonthlyRent.setText("");


                // 권리금 초기화 ('매물 종류'가 '상가, 점포'이면서 '계약 형태'가 '임대'일때만 '권리금' 항목이 보임)
                String houseValue = spinnerHouse.getSelectedItem().toString();
                TableRow tableRowPremium = rootView.findViewById(R.id.house_modify_tableRow_premium);
                tableRowPremium.setVisibility(houseValue.equals("상가, 점포") && value.equals("임대") ? View.VISIBLE : View.GONE);

                editTextPremium.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 매물 종류 초기화
        ArrayAdapter<String> arrayAdapterHouse = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterHouse.addAll(getResources().getStringArray(R.array.houseType));

        spinnerHouse.setAdapter(arrayAdapterHouse);
        spinnerHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 계약 종류 초기화
                if (position > 0) {
                    arrayAdapterPayment.clear();
                    String temp = getResources().getStringArray(R.array.paymentType)[position - 1];
                    arrayAdapterPayment.addAll(temp.split(" "));
                    spinnerPayment.setSelection(0);

                } else {
                    arrayAdapterPayment.clear();
                }


                // 시설 유무 초기화
                String value = spinnerHouse.getSelectedItem().toString();
                checkBoxFacility.setEnabled(value.equals("상가, 점포"));
                checkBoxFacility.setChecked(false);


                // 구조 초기화 ('매물 종류'가 '주택'이나 '오피스텔'일때만 '구조' 항목이 보임)
                final int visibility = value.equals("주택") || value.equals("오피스텔") ? View.VISIBLE : View.GONE;
                TableRow tableRowStructure = rootView.findViewById(R.id.house_modify_tableRow_structure);
                tableRowStructure.setVisibility(visibility);

                View view1 = rootView.findViewById(R.id.house_modify_view);
                view1.setVisibility(visibility);

                spinnerStructure.setSelection(0);


                // 화장실 위치 초기화 ('매물 종류'가 '사무실'이나 '상가, 점포'일때만 '화장실 위치' 항목이 보임)
                TableRow tableRowLocation = rootView.findViewById(R.id.house_modify_tableRow_location);
                tableRowLocation.setVisibility(value.equals("사무실") || value.equals("상가, 점포") ? View.VISIBLE : View.GONE);
                radioGroupLocation.clearCheck();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                arrayAdapterPayment.clear();
            }
        });


        // 관리비 초기화
        FlowLayout flowLayoutManageFee = rootView.findViewById(R.id.house_modify_flowLayout_manage_fee);
        checkBoxManageFee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextManageFee.setEnabled(!isChecked);
            editTextManageFee.setText("");

            flowLayoutManageFee.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });
        checkBoxManageFee.setChecked(true);

        toggleButtonGroupManageFeeContains = new ToggleButtonGroup(getContext(), "관리비 항목");
        toggleButtonGroupManageFeeContains.addToggleButtons(getResources().getStringArray(R.array.manage_fee));

        for (ToggleButton toggleButton : toggleButtonGroupManageFeeContains.getToggleButtons()) {
            flowLayoutManageFee.addView(toggleButton);
        }


        // 전용 면적, 임대 면적 초기화
        setAreaEditTexts();


        // 층 초기화
        checkBoxUnderground.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextFloor.setEnabled(!isChecked);
            editTextFloor.setText("");
        });
        checkBoxUnderground.setChecked(false);


        // 구조 초기화
        ArrayAdapter<String> arrayAdapterStructure = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterStructure.addAll(getResources().getStringArray(R.array.structure));

        spinnerStructure.setAdapter(arrayAdapterStructure);


        // 욕실 갯수 초기화
        ArrayAdapter<String> arrayAdapterBathroom = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterBathroom.addAll(getResources().getStringArray(R.array.bathroom));

        spinnerBathroom.setAdapter(arrayAdapterBathroom);


        // 방향 초기화
        ArrayAdapter<String> arrayAdapterDirection = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterDirection.addAll(getResources().getStringArray(R.array.direction));

        spinnerDirection.setAdapter(arrayAdapterDirection);


        // 준공년월 초기화
        ImageButton imageButtonBuilt = rootView.findViewById(R.id.house_modify_imageButton_built_date);
        imageButtonBuilt.setOnClickListener(v -> showDatePickerDialog((v1, result) -> textViewBuiltDate.setText(result)));


        // 입주 가능일 초기화
        ImageButton imageButtonMove = rootView.findViewById(R.id.house_modify_imageButton_move_date);
        imageButtonMove.setOnClickListener(v -> showDatePickerDialog((v12, result) -> textViewMoveDate.setText(result)));

        checkBoxMoveNow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            textViewMoveDate.setEnabled(!isChecked);
            textViewMoveDate.setText("");

            imageButtonMove.setEnabled(!isChecked);
        });
        checkBoxMoveNow.setChecked(false);


        // 옵션 정보 초기화
        toggleButtonGroupOptions = new ToggleButtonGroup(getContext(), "옵션 항목");
        toggleButtonGroupOptions.addToggleButtons(getResources().getStringArray(R.array.option));
        FlowLayout flowLayoutOption = rootView.findViewById(R.id.house_modify_flowLayout_option);
        for (ToggleButton toggleButton : toggleButtonGroupOptions.getToggleButtons()) {
            flowLayoutOption.addView(toggleButton);
        }


        // 담당자 정보 초기화
        editTextPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        return rootView;
    }

    @Override
    public boolean checkData() {
        if (textViewAddress.getText().toString().equals("")) {
            return false;
        }

        if (editTextNumber.getText().toString().equals("")) {
            return false;
        }

        if (spinnerPayment.getSelectedItemId() == 0) {
            return false;
        }

        if (spinnerHouse.getSelectedItemId() == 0) {
            return false;
        }

        TableRow tableRowPrice = rootView.findViewById(R.id.house_modify_tableRow_price);
        if (tableRowPrice.getVisibility() == View.VISIBLE && editTextPrice.getText().toString().equals("")) {
            return false;
        }

        if (tableRowPrice.getVisibility() == View.GONE
                && (editTextDeposit.getText().toString().equals("") || editTextMonthlyRent.getText().toString().equals(""))) {
            return false;
        }

        TableRow tableRowPremium = rootView.findViewById(R.id.house_modify_tableRow_premium);
        if (tableRowPremium.getVisibility() == View.VISIBLE && editTextPremium.getText().toString().equals("")) {
            return false;
        }

        if (checkBoxManageFee.isChecked() && editTextManageFee.getText().toString().equals("")) {
            return false;
        }

        if (editTextAreaMeter.getText().toString().equals("")) {
            return false;
        }

        if (editTextRentAreaMeter.getText().toString().equals("")) {
            return false;
        }

        if (editTextBuildingFloor.getText().toString().equals("")) {
            return false;
        }

        if (editTextFloor.getText().toString().equals("")) {
            return false;
        }

        TableRow tableRowStructure = rootView.findViewById(R.id.house_modify_tableRow_structure);
        if (tableRowStructure.getVisibility() == View.VISIBLE && spinnerStructure.getSelectedItemId() == 0) {
            return false;
        }

        if (spinnerBathroom.getSelectedItemId() == 0) {
            return false;
        }

        TableRow tableRowLocation = rootView.findViewById(R.id.house_modify_tableRow_location);
        if (tableRowLocation.getVisibility() == View.VISIBLE && radioGroupLocation.getCheckedRadioButtonId() == -1) {
            return false;
        }

        if (spinnerDirection.getSelectedItemId() == 0) {
            return false;
        }

        if (textViewBuiltDate.getText().toString().equals("")) {
            return false;
        }

        if (!checkBoxMoveNow.isChecked() && textViewMoveDate.getText().toString().equals("")) {
            return false;
        }

        if (editTextPhone.getText().toString().equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public void saveData() {
        House.SerializedData data = new House.SerializedData();

        data.address = textViewAddress.getText().toString();
        data.houseNumber = editTextNumber.getText().toString();
        data.paymentType = (byte) spinnerPayment.getSelectedItemPosition();
        data.houseType = (byte) spinnerHouse.getSelectedItemPosition();
        data.price = parseInt(editTextPrice.getText().toString());
        data.deposit = parseInt(editTextDeposit.getText().toString());
        data.monthlyRent = parseInt(editTextDeposit.getText().toString());
        data.premium = parseInt(editTextPremium.getText().toString());
        data.manageFee = parseInt(editTextManageFee.getText().toString());
        data.manageFeeContains = toggleButtonGroupManageFeeContains.getCheckedToggleButtonTextsInSingleLine();
        data.areaMeter = parseFloat(editTextAreaMeter.getText().toString());
        data.rentAreaMeter = parseFloat(editTextRentAreaMeter.getText().toString());
        data.buildingFloor = (byte) parseInt(editTextBuildingFloor.getText().toString());

        if (checkBoxUnderground.isChecked()) {
            data.floor = -1;

        } else {
            data.floor = Byte.parseByte(editTextFloor.getText().toString());
        }

        data.structure = (byte) spinnerStructure.getSelectedItemPosition();
        data.bathroom = (byte) spinnerBathroom.getSelectedItemPosition();
        data.bathroomLocation = (byte) radioGroupLocation.getCheckedRadioButtonId();
        data.direction = (byte) spinnerDirection.getSelectedItemId();
        data.builtDate = textViewBuiltDate.getText().toString();
        data.moveDate = textViewMoveDate.getText().toString();
        data.options = toggleButtonGroupOptions.getCheckedToggleButtonTextsInSingleLine();
        data.detailInfo = editTextPhone.getText().toString();
        data.phone = editTextPhone.getText().toString();

        SendData.getInstance().house = data;
    }

    private void setViews() {
        checkBoxFacility = rootView.findViewById(R.id.house_modify_checkBox_facility);
        checkBoxManageFee = rootView.findViewById(R.id.house_modify_checkBox_manage_fee);
        checkBoxMoveNow = rootView.findViewById(R.id.house_modify_checkBox_move_now);
        checkBoxUnderground = rootView.findViewById(R.id.house_modify_checkBox_underground);

        editTextAreaMeter = rootView.findViewById(R.id.house_modify_area_meter);
        editTextAreaPyeong = rootView.findViewById(R.id.house_modify_area_pyeong);
        editTextBuildingFloor = rootView.findViewById(R.id.house_modify_editText_building_floor);
        editTextDeposit = rootView.findViewById(R.id.house_modify_editText_deposit);
        editTextFloor = rootView.findViewById(R.id.house_modify_editText_floor);
        editTextManageFee = rootView.findViewById(R.id.house_modify_editText_manage_fee);
        editTextMonthlyRent = rootView.findViewById(R.id.house_modify_editText_monthly_rent);
        editTextNumber = rootView.findViewById(R.id.house_modify_editText_number);
        editTextPhone = rootView.findViewById(R.id.house_modify_editText_phone);
        editTextPrice = rootView.findViewById(R.id.house_modify_editText_price);
        editTextPremium = rootView.findViewById(R.id.house_modify_editText_premium);
        editTextRentAreaPyeong = rootView.findViewById(R.id.house_modify_rent_area_pyeong);
        editTextRentAreaMeter = rootView.findViewById(R.id.house_modify_rent_area_meter);

        radioGroupLocation = rootView.findViewById(R.id.house_modify_radioGroup);

        spinnerBathroom = rootView.findViewById(R.id.house_modify_spinner_bathroom);
        spinnerDirection = rootView.findViewById(R.id.house_modify_spinner_direction);
        spinnerHouse = rootView.findViewById(R.id.house_modify_spinner_houseType);
        spinnerPayment = rootView.findViewById(R.id.house_modify_spinner_paymentType);
        spinnerStructure = rootView.findViewById(R.id.house_modify_spinner_structure);

        textViewAddress = rootView.findViewById(R.id.house_modify_textView_address);
        textViewBuiltDate = rootView.findViewById(R.id.house_modify_textView_built_date);
        textViewMoveDate = rootView.findViewById(R.id.house_modify_textView_move_date);
    }

    @SuppressLint("DefaultLocale")
    private void setAreaEditTexts() {
        // 전용 면적 초기화
        editTextAreaPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextAreaPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextAreaMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextAreaMeter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextAreaMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextAreaMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextAreaPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextAreaPyeong.setText(String.format("%.2f", area * 0.3025));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // 임대 면적 초기화
        editTextRentAreaPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextRentAreaPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextRentAreaMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextRentAreaMeter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextRentAreaMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextRentAreaMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextRentAreaPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextRentAreaPyeong.setText(String.format("%.2f", area * 0.3025));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showAddressDialog() {
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

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(() -> {
                textViewAddress.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                addressDialog.dismiss();
            });
        }
    }
}