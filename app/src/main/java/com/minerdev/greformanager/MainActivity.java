package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final long FINISH_INTERVAL_TIME = 2000;
    private static final ArrayList<House> items = new ArrayList<>();
    private long backPressedTime = 0;
    private HouseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.main_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new HouseListAdapter();
        adapter.setOnItemClickListener(new OnHouseItemClickListener() {
            @Override
            public void onItemClick(HouseListAdapter.ViewHolder viewHolder, View view, int position) {
                Intent intent = new Intent(MainActivity.this, HouseDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(HouseListAdapter.ViewHolder viewHolder, View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        items.clear();

        House.SerializedData serializedData = new House.SerializedData();
        serializedData.uid = 0;
        serializedData.address = "대구시 달성군 다사읍 달구벌대로000길 00-00";
        serializedData.houseType = 1;
        serializedData.paymentType = 2;
        serializedData.state = 0;
        serializedData.price = "5억 4000";

        for (int i = 0; i < 12; i++) {
            items.add(new House(serializedData));
        }
        adapter.setItems(items);

        TabLayout tabLayout = findViewById(R.id.main_tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LinearLayout linearLayout = findViewById(R.id.main_hidden_layout);
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                LinearLayout linearLayout = findViewById(R.id.main_hidden_layout);
                int visibility = linearLayout.getVisibility();
                linearLayout.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);

        SearchView searchView = findViewById(R.id.main_searchView);
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                rearrangeList(query, group1, group2);
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.onActionViewCollapsed();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.main_menu_add:
                intent = new Intent(this, HouseModifyActivity.class);
                startActivity(intent);
                break;

            case R.id.main_menu_my_menu:
                intent = new Intent(this, MyMenuActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SearchView searchView = findViewById(R.id.main_searchView);
        if (searchView.hasFocus()) {
            searchView.onActionViewCollapsed();
            return;
        }

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            finish();

        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void rearrangeList(String keyword, int group1, int group2) {
//        if (keyword == null && group1 == 0 && group2 == 0) {
//            adapter.setItems(originalItems);
//            adapter.notifyDataSetChanged();
//            return;
//        }
//
//        items.clear();
//        for (Person i : originalItems) {
//            if ((keyword == null || i.getName().contains(keyword))
//                    && (group1 == 0 || i.getState().getName() == items1[group1])
//                    && (group2 == 0 || i.getType().getName() == items2[group2])) {
//                items.add(i);
//            }
//        }
//
//        adapter.setItems(items);
//        adapter.notifyDataSetChanged();
    }
}