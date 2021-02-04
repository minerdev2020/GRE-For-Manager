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
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class InfoFragment extends Fragment {
    private final Handler handler = new Handler();
    private TextView textView_address;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);

        textView_address = rootView.findViewById(R.id.house_modify_textView_address);

        MaterialButton button = rootView.findViewById(R.id.house_modify_materialButton_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddressDialog();
                dialog.show();
            }
        });

        Spinner spinner_payment = rootView.findViewById(R.id.house_modify_spinner_paymentType);
        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        spinner_payment.setAdapter(arrayAdapter4);

        Spinner spinner_house = rootView.findViewById(R.id.house_modify_spinner_houseType);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter1.addAll(getResources().getStringArray(R.array.houseType));
        spinner_house.setAdapter(arrayAdapter1);
        spinner_house.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        EditText editText_manage_fee = rootView.findViewById(R.id.house_modify_editText_manage_fee);
        CheckBox checkBox_manage_fee = rootView.findViewById(R.id.house_modify_checkBox_manage_fee);
        checkBox_manage_fee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editText_manage_fee.setEnabled(!isChecked);
                editText_manage_fee.setText("");

                LinearLayout linearLayout = rootView.findViewById(R.id.house_modify_manage_fee_layout);
                linearLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });
        checkBox_manage_fee.setChecked(true);

        setAreaEditTexts(rootView);

        EditText editText_floor = rootView.findViewById(R.id.house_modify_editText_floor);
        CheckBox checkBox_underground = rootView.findViewById(R.id.house_modify_checkBox_underground);
        checkBox_underground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editText_floor.setEnabled(!isChecked);
                editText_floor.setText("");
            }
        });
        checkBox_manage_fee.setChecked(true);

        Spinner spinner_structure = rootView.findViewById(R.id.house_modify_spinner_structure);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter2.addAll(getResources().getStringArray(R.array.structure));
        spinner_structure.setAdapter(arrayAdapter2);

        return rootView;
    }

    private void setAddressDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_address);

        WebView webView = dialog.findViewById(R.id.address_dialog_button_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "GREApp");
        webView.loadUrl(getString(R.string.web_server_dns) + "/get_daum_address.php");

        ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        Button button_back = dialog.findViewById(R.id.address_dialog_button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setAreaEditTexts(ViewGroup rootView) {
        EditText editText_pyeong = rootView.findViewById(R.id.house_modify_area_pyeong);
        EditText editText_meter = rootView.findViewById(R.id.house_modify_area_meter);

        editText_pyeong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText_pyeong.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editText_meter.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editText_meter.setText(String.format("%.2f", area * 3.305));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_meter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText_meter.hasFocus()) {
                    String temp = s.toString();
                    if (temp.equals("")) {
                        editText_pyeong.setText("");

                    } else {
                        float area = Float.parseFloat(temp);
                        editText_pyeong.setText(String.format("%.2f", area * 0.3025));
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
                    textView_address.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    dialog.dismiss();
                }
            });
        }
    }
}