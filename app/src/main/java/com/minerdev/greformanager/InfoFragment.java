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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.FlowLayout;

public class InfoFragment extends Fragment {
    private final Handler handler = new Handler();
    private ToggleButtonGroup manageFeeGroup;
    private ToggleButtonGroup optionGroup;
    private TextView textViewAddress;
    private Dialog addressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);



        // 주소 입력 초기화
        textViewAddress = rootView.findViewById(R.id.house_modify_textView_address);

        MaterialButton buttonSearchAddress = rootView.findViewById(R.id.house_modify_materialButton_search);
        buttonSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddressDialog();
                addressDialog.show();
            }
        });



        // 매물 종류 초기화
        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        
        Spinner spinnerPayment = rootView.findViewById(R.id.house_modify_spinner_paymentType);
        spinnerPayment.setAdapter(arrayAdapter4);



        // 계약 형태 초기화
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter1.addAll(getResources().getStringArray(R.array.houseType));
        
        Spinner spinnerHouse = rootView.findViewById(R.id.house_modify_spinner_houseType);
        spinnerHouse.setAdapter(arrayAdapter1);
        spinnerHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrayAdapter4.clear();
                String temp = getResources().getStringArray(R.array.paymentType)[position];
                arrayAdapter4.addAll(temp.split(" "));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                arrayAdapter4.clear();
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


        // 전용 면적 초기화
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
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter2.addAll(getResources().getStringArray(R.array.structure));

        Spinner spinnerStructure = rootView.findViewById(R.id.house_modify_spinner_structure);
        spinnerStructure.setAdapter(arrayAdapter2);



        // 옵션 정보 초기화
        optionGroup = new ToggleButtonGroup(getContext(), "옵션 항목");
        optionGroup.addToggleButtons(getResources().getStringArray(R.array.option));
        FlowLayout flowLayoutOption = rootView.findViewById(R.id.house_modify_flowLayout_option);
        for (ToggleButton toggleButton : optionGroup.getToggleButtons()) {
            flowLayoutOption.addView(toggleButton);
        }

        return rootView;
    }

    private void setAddressDialog() {
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

        Button button_back = addressDialog.findViewById(R.id.address_dialog_button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressDialog.dismiss();
            }
        });
    }

    private void setAreaEditTexts(ViewGroup rootView) {
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