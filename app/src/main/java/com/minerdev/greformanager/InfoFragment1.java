package com.minerdev.greformanager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.minerdev.greformanager.databinding.FragmentInfo1Binding;

public class InfoFragment1 extends Fragment implements OnSaveDataListener {
    private final Handler handler = new Handler();

    private String houseType;
    private String paymentType;

    private ArrayAdapter<String> arrayAdapterPayment;
    private ToggleButtonGroup toggleButtonGroupManageFeeContains;
    private Dialog addressDialog;
    private boolean isFirst = true;

    private FragmentInfo1Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info1, container, false);


        // 주소 입력 초기화
        binding.houseModify1MaterialButtonSearch.setOnClickListener(v -> showAddressDialog());


        // 스피너 초기화
        setSpinners();


        // 체크박스 초기화
        setCheckBoxes();


        // 관리비 초기화
        toggleButtonGroupManageFeeContains = new ToggleButtonGroup(getContext(), "관리비 항목");
        toggleButtonGroupManageFeeContains.addToggleButtons(getResources().getStringArray(R.array.manage_fee));

        for (ToggleButton toggleButton : toggleButtonGroupManageFeeContains.getToggleButtons()) {
            binding.houseModify1FlowLayoutManageFee.addView(toggleButton);
        }


        // 데이터 읽기
        Intent intent = getActivity().getIntent();
        String mode = intent.getStringExtra("mode");
        if (mode.equals("modify")) {
            House.ParcelableData data = intent.getParcelableExtra("house_value");
            readData(data);

        } else {
            if (SendData.getInstance().house != null) {
                readData(SendData.getInstance().house);
            }

            isFirst = false;
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        isFirst = true;
    }

    @Override
    public boolean checkData() {
        if (binding.houseModify1TextViewAddress.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModify1EditTextNumber.getText().toString().equals("")) {
            return false;
        }

        if (binding.houseModify1SpinnerHouseType.getSelectedItemId() == 0) {
            return false;
        }

        if (binding.houseModify1SpinnerPaymentType.getSelectedItemId() == 0) {
            return false;
        }

        if (!(paymentType.equals("월세") || paymentType.equals("단기임대") || paymentType.equals("임대"))
                && binding.houseModify1EditTextPrice.getText().toString().equals("")) {
            return false;

        } else if ((paymentType.equals("월세") || paymentType.equals("단기임대") || paymentType.equals("임대"))
                && (binding.houseModify1EditTextDeposit.getText().toString().equals("")
                || binding.houseModify1EditTextMonthlyRent.getText().toString().equals(""))) {
            return false;
        }

        if (houseType.equals("상가, 점포") && paymentType.equals("임대")
                && binding.houseModify1EditTextPremium.getText().toString().equals("")) {
            return false;
        }

        if (!binding.houseModify1CheckBoxManageFee.isChecked()
                && binding.houseModify1EditTextManageFee.getText().toString().equals("")) {
            return false;
        }

        return true;
    }

    @Override
    public void saveData() {
        House.ParcelableData data = new House.ParcelableData();

        data.address = binding.houseModify1TextViewAddress.getText().toString();
        data.house_number = binding.houseModify1EditTextNumber.getText().toString();
        data.house_type = (byte) binding.houseModify1SpinnerHouseType.getSelectedItemPosition();
        data.facility = (byte) (binding.houseModify1CheckBoxFacility.isChecked() ? 1 : 0);
        data.payment_type = (byte) binding.houseModify1SpinnerPaymentType.getSelectedItemPosition();
        data.price = parseInt(binding.houseModify1EditTextPrice.getText().toString());
        data.deposit = parseInt(binding.houseModify1EditTextDeposit.getText().toString());
        data.monthly_rent = parseInt(binding.houseModify1EditTextMonthlyRent.getText().toString());
        data.premium = parseInt(binding.houseModify1EditTextPremium.getText().toString());
        data.manage_fee = parseInt(binding.houseModify1EditTextManageFee.getText().toString());
        data.manage_fee_contains = toggleButtonGroupManageFeeContains.getCheckedToggleButtonTextsInSingleLine();

        SendData.getInstance().house = data;
    }

    private void setSpinners() {
        // 계약 형태 초기화
        arrayAdapterPayment = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        binding.houseModify1SpinnerPaymentType.setAdapter(arrayAdapterPayment);
        binding.houseModify1SpinnerPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        binding.houseModify1SpinnerHouseType.setAdapter(arrayAdapterHouse);
        binding.houseModify1SpinnerHouseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onHouseTypeItemSelected(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                arrayAdapterPayment.clear();
            }
        });
    }

    private void setCheckBoxes() {
        // 관리비 초기화
        binding.houseModify1CheckBoxManageFee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.houseModify1EditTextManageFee.setText("");
            binding.houseModify1EditTextManageFee.setEnabled(!isChecked);
            binding.houseModify1FlowLayoutManageFee.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            toggleButtonGroupManageFeeContains.resetToggleButtonCheckedState();
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

    private void onPaymentTypeItemSelected(AdapterView<?> parent, View view, int position, long id) {
        paymentType = binding.houseModify1SpinnerPaymentType.getSelectedItem().toString();

        if (paymentType.equals("월세") || paymentType.equals("단기임대") || paymentType.equals("임대")) {
            binding.houseModify1TableRowPrice.setVisibility(View.GONE);
            binding.houseModify1TableRowDeposit.setVisibility(View.VISIBLE);
            binding.houseModify1TableRowMonthlyRent.setVisibility(View.VISIBLE);

        } else {
            binding.houseModify1TableRowPrice.setVisibility(View.VISIBLE);
            binding.houseModify1TableRowDeposit.setVisibility(View.GONE);
            binding.houseModify1TableRowMonthlyRent.setVisibility(View.GONE);
        }

        if (houseType.equals("상가, 점포") && paymentType.equals("임대")) {
            binding.houseModify1TableRowPremium.setVisibility(View.VISIBLE);

        } else {
            binding.houseModify1TableRowPremium.setVisibility(View.GONE);
        }

        if (isFirst) {
            isFirst = false;

        } else {
            binding.houseModify1EditTextPrice.setText("");
            binding.houseModify1EditTextDeposit.setText("");
            binding.houseModify1EditTextMonthlyRent.setText("");
            binding.houseModify1EditTextPremium.setText("");
        }
    }

    private void onHouseTypeItemSelected(AdapterView<?> parent, View view, int position, long id) {
        houseType = binding.houseModify1SpinnerHouseType.getSelectedItem().toString();

        // 시설 유무 초기화
        binding.houseModify1CheckBoxFacility.setEnabled(houseType.equals("상가, 점포"));

        if (isFirst) {

        } else {
            if (position > 0) {
                arrayAdapterPayment.clear();
                arrayAdapterPayment.addAll(Constants.getInstance().PAYMENT_TYPE.get(position - 1));
                binding.houseModify1SpinnerPaymentType.setSelection(0);

            } else {
                arrayAdapterPayment.clear();
            }

            binding.houseModify1CheckBoxFacility.setChecked(false);
        }
    }

    private void readData(House.ParcelableData data) {
        houseType = getResources().getStringArray(R.array.houseType)[data.house_type];
        paymentType = Constants.getInstance().PAYMENT_TYPE.get(data.house_type - 1).get(data.payment_type);

        binding.houseModify1TextViewAddress.setText(data.address);
        binding.houseModify1EditTextNumber.setText(data.house_number);
        binding.houseModify1SpinnerHouseType.setSelection(data.house_type);

        arrayAdapterPayment.addAll(Constants.getInstance().PAYMENT_TYPE.get(data.house_type - 1));
        binding.houseModify1SpinnerPaymentType.setSelection(data.payment_type);

        binding.houseModify1CheckBoxFacility.setChecked(data.facility == 1);
        binding.houseModify1EditTextPrice.setText(String.valueOf(data.price));
        binding.houseModify1EditTextDeposit.setText(String.valueOf(data.deposit));
        binding.houseModify1EditTextMonthlyRent.setText(String.valueOf(data.monthly_rent));
        binding.houseModify1EditTextPremium.setText(String.valueOf(data.premium));

        if (data.manage_fee > 0) {
            binding.houseModify1CheckBoxManageFee.setChecked(false);
            binding.houseModify1EditTextManageFee.setText(String.valueOf(data.manage_fee));
        }

        if (data.manage_fee_contains != null && !data.manage_fee_contains.equals("")) {
            String[] manageFeeTexts = data.manage_fee_contains.split("\\|");
            for (String text : manageFeeTexts) {
                toggleButtonGroupManageFeeContains.setToggleButtonCheckedState(text, true);
            }
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(() -> {
                binding.houseModify1TextViewAddress.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                addressDialog.dismiss();
            });
        }
    }
}