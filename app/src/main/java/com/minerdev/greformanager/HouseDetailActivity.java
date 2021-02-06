package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;

public class HouseDetailActivity extends AppCompatActivity {
    final ImageAdapter adapter = new ImageAdapter(this);
    House.ParcelableData house;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);

        Intent intent = getIntent();
        house = intent.getParcelableExtra("house_value");

        Toolbar toolbar = findViewById(R.id.house_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readData();

        ViewPager viewPager = findViewById(R.id.house_detail_viewPager_image);
        viewPager.setAdapter(adapter);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(naverMap -> {
            naverMap.setLiteModeEnabled(true);

            UiSettings uiSettings = naverMap.getUiSettings();
            uiSettings.setZoomControlEnabled(false);
            uiSettings.setAllGesturesEnabled(false);

            Geocode.getInstance().getQueryResponseFromNaver(HouseDetailActivity.this, house.address.substring(8));
            Geocode.getInstance().setOnDataReceiveListener(result -> {
                GeocodeResult.Address address = result.addresses.get(0);
                LatLng latLng = new LatLng(Double.parseDouble(address.y), Double.parseDouble(address.x));
                naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(latLng, 16));
                Marker marker = new Marker(latLng);
                marker.setMap(naverMap);
            });
        });

        SwitchMaterial stateSwitch = findViewById(R.id.house_detail_switch);
        stateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (stateSwitch.hasFocus()) {
                house.state = (byte) (isChecked ? Constants.getInstance().SOLD_OUT : Constants.getInstance().SALE);
            }
        });
        stateSwitch.setChecked(house.state == Constants.getInstance().SOLD_OUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.house_detail_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.house_detail_menu_modify:
                Intent intent = new Intent(this, HouseModifyActivity.class);
                intent.putExtra("mode", "modify");
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void readData() {
        readImages();

        TextView address = findViewById(R.id.house_detail_textView_address);
        address.setText(house.address);

        TextView price = findViewById(R.id.house_detail_textView_price);
        price.setText(String.valueOf(house.price));

        TextView payment = findViewById(R.id.house_detail_textView_paymentType);
        payment.setText(Constants.getInstance().PAYMENT_TYPE.get(house.house_type).get(house.payment_type));

        TextView number = findViewById(R.id.house_detail_textView_number);
        number.setText(house.house_number);

        TextView detail = findViewById(R.id.house_detail_textView_detail_info);
        detail.setText(house.detail_info);

        TextView brief = findViewById(R.id.house_detail_textView_brief_info);
        brief.setText(Constants.getInstance().HOUSE_TYPE.get(house.house_type));
    }

    void readImages() {
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
    }
}