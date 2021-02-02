package com.minerdev.greformanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class InfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);

        Spinner spinner_address1 = rootView.findViewById(R.id.house_modify_spinner_address1);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter1.addAll(getResources().getStringArray(R.array.address1));
        spinner_address1.setAdapter(arrayAdapter1);

        Spinner spinner_address2 = rootView.findViewById(R.id.house_modify_spinner_address2);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter2.addAll(getResources().getStringArray(R.array.address1));
        spinner_address2.setAdapter(arrayAdapter2);

        Spinner spinner_address3 = rootView.findViewById(R.id.house_modify_spinner_address3);
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter3.addAll(getResources().getStringArray(R.array.address1));
        spinner_address3.setAdapter(arrayAdapter3);

        Spinner spinner_payment = rootView.findViewById(R.id.house_modify_spinner_paymentType);
        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter4.addAll(getResources().getStringArray(R.array.paymentType));
        spinner_payment.setAdapter(arrayAdapter4);

        return rootView;
    }
}