package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minerdev.greformanager.databinding.ActivityHouseDetailBinding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;

public class HouseDetailActivity extends AppCompatActivity {
    private final ImageAdapter adapter = new ImageAdapter();
    private HouseWrapper houseWrapper;
    private int originalState;

    private ActivityHouseDetailBinding binding;
    private ImageViewModel imageViewModel;

    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_detail);
        binding.setActivity(this);

        imageViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ImageViewModel.class);


        Intent intent = getIntent();
        House data = intent.getParcelableExtra("house_value");
        originalState = data.state;
        houseWrapper = new HouseWrapper(data);


        setSupportActionBar(binding.houseDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewModel.getOrderByPosition(data.id).observe(this, images -> {
            Log.d("DB_CHANGED", "observe");

            for (Image image : images) {
                Log.d("DB_CHANGED", image.title);
            }

            adapter.addImages(images);
            adapter.notifyDataSetChanged();
        });

        binding.houseDetailViewPager2Image.setAdapter(adapter);


        setMapFragment();


        ToggleButtonGroup toggleButtonGroupManageFee = new ToggleButtonGroup(this, "ManageFee");
        toggleButtonGroupManageFee.addToggleButtonsFromText(houseWrapper.getManageFeeContains());
        for (ToggleButton toggleButton : toggleButtonGroupManageFee.getToggleButtons()) {
            binding.houseDetailFlowLayoutManageFee.addView(toggleButton);
        }

        ToggleButtonGroup toggleButtonGroupOptions = new ToggleButtonGroup(this, "Options");
        toggleButtonGroupOptions.addToggleButtonsFromText(houseWrapper.getOptions());
        for (ToggleButton toggleButton : toggleButtonGroupOptions.getToggleButtons()) {
            binding.houseDetailFlowLayoutOptions.addView(toggleButton);
        }


        readItems();
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
                intent.putExtra("house_value", houseWrapper.getData());
                startActivityForResult(intent, Constants.getInstance().HOUSE_MODIFY_ACTIVITY_REQUEST_CODE);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        if (originalState != houseWrapper.getData().state) {
            JsonObject data = new JsonObject();
            data.addProperty("state", houseWrapper.getData().state);
            HttpConnection.getInstance().send(this, Request.Method.PATCH,
                    "houses/" + houseWrapper.getData().id, data, null);
        }

        Intent intent = new Intent();
        intent.putExtra("house_value", houseWrapper.getData());
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

                HouseWrapper houseWrapper = new HouseWrapper(data.getParcelableExtra("house_value"));
                this.houseWrapper = houseWrapper;
                refreshUI();
            }
        }
    }

    public HouseWrapper getHouseWrapper() {
        return houseWrapper;
    }

    private void refreshUI() {
        binding.invalidateAll();
        binding.houseDetailFlowLayoutManageFee.removeAllViews();
        binding.houseDetailFlowLayoutOptions.removeAllViews();

        ToggleButtonGroup toggleButtonGroupManageFee = new ToggleButtonGroup(this, "ManageFee");
        toggleButtonGroupManageFee.addToggleButtons(houseWrapper.getManageFeeContains().split("\\|"));
        for (ToggleButton toggleButton : toggleButtonGroupManageFee.getToggleButtons()) {
            binding.houseDetailFlowLayoutManageFee.addView(toggleButton);
        }

        ToggleButtonGroup toggleButtonGroupOptions = new ToggleButtonGroup(this, "Options");
        toggleButtonGroupOptions.addToggleButtons(houseWrapper.getOptions().split("\\|"));
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

            Geocode.getInstance().getQueryResponseFromNaver(HouseDetailActivity.this, houseWrapper.getAddress().substring(8));
            Geocode.getInstance().setOnDataReceiveListener(result -> {
                GeocodeResult.Address address = result.addresses.get(0);
                LatLng latLng = new LatLng(Double.parseDouble(address.y), Double.parseDouble(address.x));
                naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(latLng, 16));
                Marker marker = new Marker(latLng);
                marker.setMap(naverMap);
            });
        });
    }

    private void readItems() {
        HttpConnection.getInstance().receive(this, "houses/" + houseWrapper.getId() + "/images",
                receivedData -> {
                    Gson gson = new Gson();
                    Image[] array = gson.fromJson(receivedData, Image[].class);
                    if (array == null) {
                        Toast.makeText(this, "데이터 수신 실패.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d("DB_DATA", receivedData);

                    imageViewModel.insert(array);
                });
    }
}