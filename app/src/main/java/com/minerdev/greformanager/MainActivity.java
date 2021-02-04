package com.minerdev.greformanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
//    private static final String[] tabTitles = {"건물형태", "매물종류", "보증금", "월세금", "방 개수", "층수", "평수", "추가옵션"};
//    private static final String[][] toggleButtonTitles = {
//            {"빌라/주택", "오피스텔", "아파트", "상가/사무실"},
//            {"월세", "전세", "매매", "단기임대"},
//            null,
//            null,
//            {"원룸", "투룸", "쓰리룸 이상"},
//            {"1층~5층", "6층 이상", "반지하", "옥탑", "복층"},
//            {"5평 이하", "6~10평", "11평 이상"},
//            {"신축", "풀옵션", "주차가능", "엘레베이터", "반려동물", "전세자금대출", "큰길가", "권리분석"}
//    };

    private static final long FINISH_INTERVAL_TIME = 2000;
    private static final ArrayList<House> items = new ArrayList<>();
    private final HouseListAdapter saleAdapter = new HouseListAdapter();
    private final HouseListAdapter soldAdapter = new HouseListAdapter();

    private long backPressedTime = 0;
    private OneSideDrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // 툴바 초기화
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);



        // 판매중 매물 RecyclerView 및 HouseListAdapter 초기화
        RecyclerView saleRecyclerView = findViewById(R.id.main_recyclerView_sale);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        saleRecyclerView.setLayoutManager(manager);
        saleRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        saleAdapter.setOnItemClickListener(new HouseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HouseListAdapter.ViewHolder viewHolder, View view, int position) {
                String address = saleAdapter.getItem(position).getAddress();

                Intent intent = new Intent(MainActivity.this, HouseDetailActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
        saleRecyclerView.setAdapter(saleAdapter);

        ImageButton imageButton = findViewById(R.id.main_imageButton_sale_expand);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView recyclerView = findViewById(R.id.main_recyclerView_sale);
                recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                imageButton.setImageResource(recyclerView.getVisibility() == View.VISIBLE ? R.drawable.ic_round_expand_less_24 : R.drawable.ic_round_expand_more_24);
            }
        });



        // 판매완료 매물 RecyclerView 및 HouseListAdapter 초기화
        RecyclerView soldRecyclerView = findViewById(R.id.main_recyclerView_sold);
        LinearLayoutManager manager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        soldRecyclerView.setLayoutManager(manager1);
        soldRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        soldAdapter.setOnItemClickListener(new HouseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HouseListAdapter.ViewHolder viewHolder, View view, int position) {
                String address = soldAdapter.getItem(position).getAddress();

                Intent intent = new Intent(MainActivity.this, HouseDetailActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
        soldRecyclerView.setAdapter(soldAdapter);

        ImageButton imageButton1 = findViewById(R.id.main_imageButton_sold_expand);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView recyclerView = findViewById(R.id.main_recyclerView_sold);
                recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                imageButton1.setImageResource(recyclerView.getVisibility() == View.VISIBLE ? R.drawable.ic_round_expand_less_24 : R.drawable.ic_round_expand_more_24);
            }
        });



        // NavigationView 초기화
        setNavigationView();



        // 매물 리스트 초기화 및 HouseListAdapter에 리스트 등록
        items.clear();

        House.SerializedData serializedData = new House.SerializedData();
        serializedData.uid = 0;
        serializedData.address = "대구광역시 달성군 다사읍 달구벌대로174길 10-17";
        serializedData.state = 0;
        serializedData.price = "5억 4000";
        serializedData.paymentType = 1;
        serializedData.houseType = 1;
        serializedData.houseNumber = "1";

        for (int i = 0; i < 12; i++) {
            items.add(new House(serializedData));
        }
        saleAdapter.setItems(items);
        soldAdapter.setItems(items);
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
                rearrangeList(query);
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

                saleAdapter.setItems(items);
                saleAdapter.notifyDataSetChanged();

                soldAdapter.setItems(items);
                soldAdapter.notifyDataSetChanged();

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
                intent.putExtra("mode", "add");
                startActivity(intent);
                break;

            case R.id.main_menu_my_menu:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawers();
            return;
        }

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

    private void rearrangeList(String keyword) {
        ArrayList<House> temp = new ArrayList<>();
        for (House i : items) {
            if ((keyword == null || i.getHouseNumber().contains(keyword) || i.getAddress().contains(keyword))) {
                temp.add(i);
            }
        }

        saleAdapter.setItems(temp);
        saleAdapter.notifyDataSetChanged();

        soldAdapter.setItems(temp);
        soldAdapter.notifyDataSetChanged();
    }

    private void setNavigationView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.lockSwipe(GravityCompat.END);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_logout:
                        SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.nav_menu_exit:
                        finish();
                        break;

                    default:
                        break;
                }

                return true;
            }
        });
    }
}