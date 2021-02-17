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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final long FINISH_INTERVAL_TIME = 2000;
    private final HouseListAdapter saleAdapter = new HouseListAdapter();
    private final HouseListAdapter soldAdapter = new HouseListAdapter();

    private long backPressedTime = 0;
    private OneSideDrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.getInstance().initialize(getApplicationContext());

        // 툴바 초기화
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


        // 판매중 매물 RecyclerView 및 HouseListAdapter 초기화
        RecyclerView saleRecyclerView = findViewById(R.id.main_recyclerView_sale);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        saleRecyclerView.setLayoutManager(manager);
        saleRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        saleAdapter.setOnItemClickListener((viewHolder, view, position) -> {
            Intent intent = new Intent(MainActivity.this, HouseDetailActivity.class);
            intent.putExtra("original_state", Constants.getInstance().SALE);
            intent.putExtra("index", position);
            intent.putExtra("house_value", saleAdapter.getItem(position));
            startActivityForResult(intent, Constants.getInstance().HOUSE_DETAIL_ACTIVITY_REQUEST_CODE);
        });
        saleRecyclerView.setAdapter(saleAdapter);

        ImageButton imageButton = findViewById(R.id.main_imageButton_sale_expand);
        imageButton.setOnClickListener(v -> {
            saleRecyclerView.setVisibility(saleRecyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            imageButton.setImageResource(saleRecyclerView.getVisibility() == View.VISIBLE ? R.drawable.ic_round_expand_less_24 : R.drawable.ic_round_expand_more_24);
        });


        // 판매완료 매물 RecyclerView 및 HouseListAdapter 초기화
        RecyclerView soldRecyclerView = findViewById(R.id.main_recyclerView_sold);
        LinearLayoutManager manager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        soldRecyclerView.setLayoutManager(manager1);
        soldRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        soldAdapter.setOnItemClickListener((viewHolder, view, position) -> {
            Intent intent = new Intent(MainActivity.this, HouseDetailActivity.class);
            intent.putExtra("original_state", Constants.getInstance().SOLD);
            intent.putExtra("index", position);
            intent.putExtra("house_value", soldAdapter.getItem(position));
            startActivityForResult(intent, Constants.getInstance().HOUSE_DETAIL_ACTIVITY_REQUEST_CODE);
        });
        soldRecyclerView.setAdapter(soldAdapter);

        ImageButton imageButton1 = findViewById(R.id.main_imageButton_sold_expand);
        imageButton1.setOnClickListener(v -> {
            soldRecyclerView.setVisibility(soldRecyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            imageButton1.setImageResource(soldRecyclerView.getVisibility() == View.VISIBLE ? R.drawable.ic_round_expand_less_24 : R.drawable.ic_round_expand_more_24);
        });


        // NavigationView 초기화
        setNavigationView();


        readItems();
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
        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();

            saleAdapter.resetSearchResults();
            saleAdapter.notifyDataSetChanged();

            soldAdapter.resetSearchResults();
            soldAdapter.notifyDataSetChanged();

            return true;
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
                startActivityForResult(intent, Constants.getInstance().HOUSE_MODIFY_ACTIVITY_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.getInstance().HOUSE_DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }

                int originalState = data.getIntExtra("original_state", 0);
                int index = data.getIntExtra("index", 0);
                HouseParcelableData house = data.getParcelableExtra("house_value");

                if (originalState != house.state) {
                    if (originalState == Constants.getInstance().SALE) {
                        saleAdapter.removeItem(index);
                        saleAdapter.notifyItemRemoved(index);

                        soldAdapter.addItem(house);
                        soldAdapter.notifyDataSetChanged();

                    } else {
                        soldAdapter.removeItem(index);
                        soldAdapter.notifyItemRemoved(index);

                        saleAdapter.addItem(house);
                        saleAdapter.notifyDataSetChanged();
                    }

                } else {
                    if (house.state == Constants.getInstance().SALE) {
                        saleAdapter.setItem(index, house);
                        saleAdapter.notifyItemChanged(index);

                    } else {
                        soldAdapter.setItem(index, house);
                        soldAdapter.notifyItemChanged(index);
                    }
                }
            }

        } else if (requestCode == Constants.getInstance().HOUSE_MODIFY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }

                HouseParcelableData house = data.getParcelableExtra("house_value");
                saleAdapter.addItem(house);
                saleAdapter.notifyDataSetChanged();
            }
        }
    }

    private void rearrangeList(String keyword) {
        saleAdapter.searchItems(keyword);
        saleAdapter.notifyDataSetChanged();

        soldAdapter.searchItems(keyword);
        soldAdapter.notifyDataSetChanged();
    }

    private void setNavigationView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.lockSwipe(GravityCompat.END);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();

            switch (menuItem.getItemId()) {
                case R.id.nav_menu_logout:
                    SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.clear();
                    editor.apply();

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
        });
    }

    private void readItems() {
        HttpConnection.getInstance().receive(this, "houses",
                receivedData -> {
                    Gson gson = new Gson();
                    HouseParcelableData[] array = gson.fromJson(receivedData, HouseParcelableData[].class);
                    if (array == null) {
                        Toast.makeText(this, "데이터 수신 실패.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<HouseParcelableData> list = new ArrayList<>();
                    Collections.addAll(list, array);

                    saleAdapter.clearItems();
                    soldAdapter.clearItems();

                    // 매물의 판매 상태에 따라 분류
                    for (HouseParcelableData item : list) {
                        if (item.state == Constants.getInstance().SALE) {
                            saleAdapter.addItem(item);

                        } else {
                            soldAdapter.addItem(item);
                        }
                    }

                    saleAdapter.notifyDataSetChanged();
                    soldAdapter.notifyDataSetChanged();
                });
    }
}