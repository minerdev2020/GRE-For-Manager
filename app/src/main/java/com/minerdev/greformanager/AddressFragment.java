package com.minerdev.greformanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class AddressFragment extends Fragment {
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_address, container, false);

        EditText editText = rootView.findViewById(R.id.house_modify_editView_address);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 2) {
                    Geocode.getInstance().getQueryResponseFromNaver(getContext(), s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageButton imageButton = rootView.findViewById(R.id.house_modify_imageButton_search);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (text.length() >= 2) {
                    Geocode.getInstance().getQueryResponseFromNaver(getContext(), text);
                }
            }
        });

        ListView listView = rootView.findViewById(R.id.house_modify_listView_address);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText(parent.getItemAtPosition(position).toString() + " ");
                editText.setSelection(editText.length());
            }
        });
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);

        Geocode.getInstance().setOnDataReceiveCallback(new Geocode.OnDataReceiveCallback() {
            @Override
            public void parseData(GeocodeResult result) {
                if (result.addresses.size() > 0) {
                    ArrayList<String> roadAddresses = new ArrayList<>();

                    for (GeocodeResult.Address address : result.addresses) {
                        roadAddresses.add(address.roadAddress);
                    }

                    Log.d("RESULT", roadAddresses.toString());
                    arrayAdapter.clear();
                    arrayAdapter.addAll(roadAddresses);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }
}