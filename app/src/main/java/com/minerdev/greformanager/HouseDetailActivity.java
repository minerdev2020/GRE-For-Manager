package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;

public class HouseDetailActivity extends AppCompatActivity {
    private final ImageAdapter adapter = new ImageAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);

        Toolbar toolbar = findViewById(R.id.house_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readImages();

        ViewPager viewPager = findViewById(R.id.image_slider_viewPager_image);
        viewPager.setAdapter(adapter);

        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        String client_id = getString(R.string.client_id);
        String client_secret = getString(R.string.client_secret);

        Geocoding.getInstance().getPointFromNaver(this, client_id, client_secret, address);
        Geocoding.Point point = Geocoding.getInstance().getPoint();
        LatLng latLng = new LatLng(point.x, point.y);

        Log.d("Geocoding", point.toString());

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NaverMap naverMap) {
                naverMap.setLiteModeEnabled(true);
                naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(latLng, 15));

                UiSettings uiSettings = naverMap.getUiSettings();
                uiSettings.setZoomControlEnabled(false);
                uiSettings.setAllGesturesEnabled(false);

                Marker marker = new Marker(latLng);
                marker.setMap(naverMap);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.house_detail_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.house_detail_menu_modify:
                intent = new Intent(this, HouseModifyActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readImages() {
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
        adapter.addImage(R.drawable.house);
    }
}