package com.minerdev.greformanager;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class HouseModifyAdapter extends PagerAdapter {
    private final ArrayList<Integer> layoutList = new ArrayList<>();

    private ArrayAdapter<String> arrayAdapter;
    private Context context;

    public HouseModifyAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;

        if (context != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutList.get(position), container, false);

            switch (layoutList.get(position)) {
                case R.layout.house_modify_input_address:
                    setAddressPage(view);
                    break;

                case R.layout.house_modify_select_images:
                    setImagePage(view);
                    break;

                case R.layout.house_modify_input_info:
                    setInfoPage(view);
                    break;

                default:
                    break;
            }
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View) object);
    }

    @Override
    public int getCount() {
        return layoutList.size();
    }

    public void addLayout(Integer resId) {
        layoutList.add(resId);
    }

    private void setAddressPage(View view) {
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        EditText editText = view.findViewById(R.id.house_modify_editView_address);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 2) {
                    Geocode.getInstance().getQueryResponseFromNaver(context, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ListView listView = view.findViewById(R.id.house_modify_listView_address);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText(parent.getItemAtPosition(position).toString());
            }
        });
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
    }

    private void setImagePage(View view) {

    }

    private void setInfoPage(View view) {

    }
}
