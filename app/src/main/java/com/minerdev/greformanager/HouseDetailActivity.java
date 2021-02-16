package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;

import com.android.volley.Request;
import com.google.gson.JsonObject;
import com.minerdev.greformanager.databinding.ActivityHouseDetailBinding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;

public class HouseDetailActivity extends AppCompatActivity {
    private final ImageAdapter adapter = new ImageAdapter(this);
    private House house;
    private int index;
    private int originalState;

    private ActivityHouseDetailBinding binding;

    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_detail);
        binding.setActivity(this);

        Intent intent = getIntent();
        originalState = intent.getIntExtra("original_state", 0);
        index = intent.getIntExtra("index", 0);
        House.ParcelableData data = intent.getParcelableExtra("house_value");
        house = new House(data);


        setSupportActionBar(binding.houseDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readImages();

        binding.houseDetailViewPagerImage.setAdapter(adapter);

        setMapFragment();

        ToggleButtonGroup toggleButtonGroupManageFee = new ToggleButtonGroup(this, "ManageFee");
        toggleButtonGroupManageFee.addToggleButtonsFromText(house.getManageFeeContains());
        for (ToggleButton toggleButton : toggleButtonGroupManageFee.getToggleButtons()) {
            binding.houseDetailFlowLayoutManageFee.addView(toggleButton);
        }

        ToggleButtonGroup toggleButtonGroupOptions = new ToggleButtonGroup(this, "Options");
        toggleButtonGroupOptions.addToggleButtonsFromText(house.getOptions());
        for (ToggleButton toggleButton : toggleButtonGroupOptions.getToggleButtons()) {
            binding.houseDetailFlowLayoutOptions.addView(toggleButton);
        }
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
                intent.putExtra("house_value", house.getData());
                startActivityForResult(intent, Constants.getInstance().HOUSE_MODIFY_ACTIVITY_REQUEST_CODE);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        if (originalState != house.getData().state) {
            JsonObject data = new JsonObject();
            data.addProperty("state", house.getData().state);
            HttpConnection.getInstance().send(this, Request.Method.PATCH,
                    "houses/" + house.getData().id, data, null);
        }

        Intent intent = new Intent();
        intent.putExtra("original_state", originalState);
        intent.putExtra("index", index);
        intent.putExtra("house_value", house.getData());
        setResult(RESULT_OK, intent);

        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.getInstance().HOUSE_MODIFY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }

                House house = new House(data.getParcelableExtra("house_value"));
                this.house = house;
                refreshUI();
            }
        }
    }

    public House getHouse() {
        return house;
    }

    private void refreshUI() {
        binding.invalidateAll();
        binding.houseDetailFlowLayoutManageFee.removeAllViews();
        binding.houseDetailFlowLayoutOptions.removeAllViews();

        ToggleButtonGroup toggleButtonGroupManageFee = new ToggleButtonGroup(this, "ManageFee");
        toggleButtonGroupManageFee.addToggleButtons(house.getManageFeeContains().split("\\|"));
        for (ToggleButton toggleButton : toggleButtonGroupManageFee.getToggleButtons()) {
            binding.houseDetailFlowLayoutManageFee.addView(toggleButton);
        }

        ToggleButtonGroup toggleButtonGroupOptions = new ToggleButtonGroup(this, "Options");
        toggleButtonGroupOptions.addToggleButtons(house.getOptions().split("\\|"));
        for (ToggleButton toggleButton : toggleButtonGroupOptions.getToggleButtons()) {
            binding.houseDetailFlowLayoutOptions.addView(toggleButton);
        }

        setMapFragment();
    }

    private void setMapFragment() {
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

            Geocode.getInstance().getQueryResponseFromNaver(HouseDetailActivity.this, house.getAddress().substring(8));
            Geocode.getInstance().setOnDataReceiveListener(result -> {
                GeocodeResult.Address address = result.addresses.get(0);
                LatLng latLng = new LatLng(Double.parseDouble(address.y), Double.parseDouble(address.x));
                naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(latLng, 16));
                Marker marker = new Marker(latLng);
                marker.setMap(naverMap);
            });
        });
    }

    private void readImages() {
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
    }
}