package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
    House.SerializedData house;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);

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

            Intent intent = getIntent();
            String address = intent.getStringExtra("address");

            Geocode.getInstance().getQueryResponseFromNaver(HouseDetailActivity.this, address);
            Geocode.getInstance().setOnDataReceiveListener(result -> {
                GeocodeResult.Address address1 = result.addresses.get(0);
                LatLng latLng = new LatLng(Double.parseDouble(address1.y), Double.parseDouble(address1.x));
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
        stateSwitch.setChecked(house.state != Constants.getInstance().SOLD_OUT);
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
    }

    void readImages() {
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
    }
}