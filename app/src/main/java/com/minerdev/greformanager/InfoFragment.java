package com.minerdev.greformanager;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.FlowLayout;

public class InfoFragment extends Fragment implements OnSaveDataListener {
    private final Handler handler = new Handler();
    private ToggleButtonGroup manageFeeGroup;
    private ToggleButtonGroup optionGroup;
    private ViewGroup rootView;

    private TextView textViewAddress;
    private Dialog addressDialog;
    private Spinner spinnerPayment;
    private Spinner spinnerHouse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);


        // 주소 입력 초기화
        textViewAddress = rootView.findViewById(R.id.house_modify_textView_address);

        MaterialButton buttonSearchAddress = rootView.findViewById(R.id.house_modify_materialButton_search);
        buttonSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressDialog();
            }
        });


        // 계약 형태 초기화
        ArrayAdapter<String> arrayAdapterPayment = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);

        spinnerPayment = rootView.findViewById(R.id.house_modify_spinner_paymentType);
        spinnerPayment.setAdapter(arrayAdapterPayment);
        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TableRow tableRowPrice = rootView.findViewById(R.id.house_modify_tableRow_price);
                TableRow tableRowDeposit = rootView.findViewById(R.id.house_modify_tableRow_deposit);
                TableRow tableRowMonthlyRent = rootView.findViewById(R.id.house_modify_tableRow_monthly_rent);
                TableRow tableRowMonthlyPremium = rootView.findViewById(R.id.house_modify_tableRow_premium);

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

                String houseValue = spinnerHouse.getSelectedItem().toString();
                tableRowMonthlyPremium.setVisibility(houseValue.equals("상가, 점포") && value.equals("임대") ? View.VISIBLE : View.GONE);

                EditText editTextPrice = rootView.findViewById(R.id.house_modify_editText_price);
                EditText editTextDeposit = rootView.findViewById(R.id.house_modify_editText_deposit);
                EditText editTextMonthlyRent = rootView.findViewById(R.id.house_modify_editText_monthly_rent);
                EditText editTextPremium = rootView.findViewById(R.id.house_modify_editText_premium);
                editTextPrice.setText("");
                editTextDeposit.setText("");
                editTextMonthlyRent.setText("");
                editTextPremium.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 매물 종류 초기화
        ArrayAdapter<String> arrayAdapterHouse = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterHouse.addAll(getResources().getStringArray(R.array.houseType));

        spinnerHouse = rootView.findViewById(R.id.house_modify_spinner_houseType);
        spinnerHouse.setAdapter(arrayAdapterHouse);
        spinnerHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    arrayAdapterPayment.clear();
                    String temp = getResources().getStringArray(R.array.paymentType)[position - 1];
                    arrayAdapterPayment.addAll(temp.split(" "));
                    spinnerPayment.setSelection(0);

                } else {
                    arrayAdapterPayment.clear();
                }

                String value = spinnerHouse.getSelectedItem().toString();
                CheckBox checkBox = rootView.findViewById(R.id.house_modify_checkBox_facility);
                checkBox.setEnabled(value.equals("상가, 점포") ? true : false);
                checkBox.setChecked(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                arrayAdapterPayment.clear();
            }
        });


        // 관리비 초기화
        EditText editTextManageFee = rootView.findViewById(R.id.house_modify_editText_manage_fee);

        CheckBox checkBoxManageFee = rootView.findViewById(R.id.house_modify_checkBox_manage_fee);
        checkBoxManageFee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTextManageFee.setEnabled(!isChecked);
                editTextManageFee.setText("");

                FlowLayout layout = rootView.findViewById(R.id.house_modify_flowLayout_manage_fee);
                layout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });
        checkBoxManageFee.setChecked(true);

        manageFeeGroup = new ToggleButtonGroup(getContext(), "관리비 항목");
        manageFeeGroup.addToggleButtons(getResources().getStringArray(R.array.manage_fee));
        FlowLayout flowLayoutManageFee = rootView.findViewById(R.id.house_modify_flowLayout_manage_fee);
        for (ToggleButton toggleButton : manageFeeGroup.getToggleButtons()) {
            flowLayoutManageFee.addView(toggleButton);
        }


        // 전용 면적, 임대 면적 초기화
        setAreaEditTexts(rootView);


        // 층 초기화
        EditText editTextFloor = rootView.findViewById(R.id.house_modify_editText_floor);

        CheckBox checkBoxUnderground = rootView.findViewById(R.id.house_modify_checkBox_underground);
        checkBoxUnderground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTextFloor.setEnabled(!isChecked);
                editTextFloor.setText("");
            }
        });
        checkBoxUnderground.setChecked(false);


        // 구조 초기화
        ArrayAdapter<String> arrayAdapterStructure = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterStructure.addAll(getResources().getStringArray(R.array.structure));

        Spinner spinnerStructure = rootView.findViewById(R.id.house_modify_spinner_structure);
        spinnerStructure.setAdapter(arrayAdapterStructure);


        // 욕실 갯수 초기화
        ArrayAdapter<String> arrayAdapterBathroom = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterBathroom.addAll(getResources().getStringArray(R.array.bathroom));

        Spinner spinnerBathroom = rootView.findViewById(R.id.house_modify_spinner_bathroom);
        spinnerBathroom.setAdapter(arrayAdapterBathroom);


        // 방향 초기화
        ArrayAdapter<String> arrayAdapterDirection = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterDirection.addAll(getResources().getStringArray(R.array.direction));

        Spinner spinnerDirection = rootView.findViewById(R.id.house_modify_spinner_direction);
        spinnerDirection.setAdapter(arrayAdapterDirection);


        // 입주 가능일 초기화
        TextView textViewBuilt = rootView.findViewById(R.id.house_modify_textView_built_date);

        ImageButton imageButtonBuilt = rootView.findViewById(R.id.house_modify_imageButton_built_date);
        imageButtonBuilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(new OnClickListener() {
                    @Override
                    public void onClick(View v, String result) {
                        textViewBuilt.setText(result);
                    }
                });
            }
        });


        // 입주 가능일 초기화
        TextView textViewMove = rootView.findViewById(R.id.house_modify_textView_move_date);

        ImageButton imageButtonMove = rootView.findViewById(R.id.house_modify_imageButton_move_date);
        imageButtonMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(new OnClickListener() {
                    @Override
                    public void onClick(View v, String result) {
                        textViewMove.setText(result);
                    }
                });
            }
        });

        CheckBox checkBoxMoveNow = rootView.findViewById(R.id.house_modify_checkBox_move_now);
        checkBoxMoveNow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                textViewMove.setEnabled(!isChecked);
                textViewMove.setText("");

                imageButtonMove.setEnabled(!isChecked);
            }
        });
        checkBoxMoveNow.setChecked(false);


        // 옵션 정보 초기화
        optionGroup = new ToggleButtonGroup(getContext(), "옵션 항목");
        optionGroup.addToggleButtons(getResources().getStringArray(R.array.option));
        FlowLayout flowLayoutOption = rootView.findViewById(R.id.house_modify_flowLayout_option);
        for (ToggleButton toggleButton : optionGroup.getToggleButtons()) {
            flowLayoutOption.addView(toggleButton);
        }

        return rootView;
    }

    @Override
    public void saveData() {
        House.SerializedData data = new House.SerializedData();
        data.houseNumber = "";

        EditText price = rootView.findViewById(R.id.house_modify_editText_price);
        data.price = price.getText().toString();

        TextView address = rootView.findViewById(R.id.house_modify_textView_address);
        data.address = address.getText().toString();

        data.state = 0;

        Spinner houseType = rootView.findViewById(R.id.house_modify_spinner_houseType);
        data.houseType = (byte) houseType.getSelectedItemPosition();

        Spinner paymentType = rootView.findViewById(R.id.house_modify_spinner_paymentType);
        data.paymentType = (byte) paymentType.getSelectedItemPosition();

//        data.deposit;
//        data.monthlyRent;
//
//        data.isContainManageFee;

        Spinner roomCount = rootView.findViewById(R.id.house_modify_spinner_structure);
        data.roomCount = (byte) roomCount.getSelectedItemPosition();

        CheckBox underground = rootView.findViewById(R.id.house_modify_checkBox_underground);
        if (underground.isChecked()) {
            data.floor = -1;

        } else {
            EditText floor = rootView.findViewById(R.id.house_modify_editText_floor);
            data.floor = Byte.parseByte(floor.getText().toString());
        }

        EditText area = rootView.findViewById(R.id.house_modify_area_pyeong);
        data.area = Float.parseFloat(area.getText().toString());

//        data.extra;

        SendData.getInstance().house = data;
    }

    private void setAreaEditTexts(ViewGroup rootView) {
        // 전용 면적 초기화
        EditText editTextPyeong = rootView.findViewById(R.id.house_modify_area_pyeong);
        EditText editTextMeter = rootView.findViewById(R.id.house_modify_area_meter);

        editTextPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextMeter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextPyeong.setText(String.format("%.2f", area * 0.3025));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // 임대 면적 초기화
        EditText editTextRentPyeong = rootView.findViewById(R.id.house_modify_rent_area_pyeong);
        EditText editTextRentMeter = rootView.findViewById(R.id.house_modify_rent_area_meter);

        editTextRentPyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextRentPyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextRentMeter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextRentMeter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextRentMeter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextRentMeter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editTextRentPyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editTextRentPyeong.setText(String.format("%.2f", area * 0.3025));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showAddressDialog() {
        addressDialog = new Dialog(getContext());
        addressDialog.setContentView(R.layout.dialog_address);

        WebView webView = addressDialog.findViewById(R.id.address_dialog_button_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "GREApp");
        webView.loadUrl(getString(R.string.web_server_dns) + "/get_daum_address.php");

        ViewGroup.LayoutParams params = addressDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        addressDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        Button buttonBack = addressDialog.findViewById(R.id.address_dialog_button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressDialog.dismiss();
            }
        });

        addressDialog.show();
    }

    private void showDatePickerDialog(OnClickListener listener) {
        Dialog datePickerDialog = new Dialog(getContext());
        datePickerDialog.setContentView(R.layout.dialog_date_picker);

        Button buttonBack = datePickerDialog.findViewById(R.id.date_picker_dialog_button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.dismiss();
            }
        });

        Button buttonSelect = datePickerDialog.findViewById(R.id.date_picker_dialog_button_select);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = datePickerDialog.findViewById(R.id.date_picker_dialog_datePicker);
                String date = datePicker.getYear() + "." + datePicker.getMonth() + "." + datePicker.getDayOfMonth();
                if (listener != null) {
                    listener.onClick(v, date);
                }

                datePickerDialog.dismiss();
            }
        });

        datePickerDialog.show();
    }

    public interface OnClickListener {
        void onClick(View v, String result);
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textViewAddress.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    addressDialog.dismiss();
                }
            });
        }
    }
}