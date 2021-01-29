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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.internal.FlowLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String[] tabTitles = {"건물형태", "매물종류", "보증금", "월세금", "방 개수", "층수", "평수", "추가옵션"};
    private static final String[][] toggleButtonTitles = {
            {"빌라/주택", "오피스텔", "아파트", "상가/사무실"},
            {"월세", "전세", "매매", "단기임대"},
            null,
            null,
            {"원룸", "투룸", "쓰리룸 이상"},
            {"1층~5층", "6층 이상", "반지하", "옥탑", "복층"},
            {"5평 이하", "6~10평", "11평 이상"},
            {"신축", "풀옵션", "주차가능", "엘레베이터", "반려동물", "전세자금대출", "큰길가", "권리분석"}
    };

    private static final int FILTER_ACTIVITY_REQUEST_CODE = 0;
    private static final long FINISH_INTERVAL_TIME = 2000;
    private static final ArrayList<House> items = new ArrayList<>();
    private final HouseListAdapter adapter = new HouseListAdapter();
    private final ArrayList<ToggleButtonGroup> toggleButtonGroups = new ArrayList<>();

    private long backPressedTime = 0;
    private TabLayout tabLayout;
    private OneSideDrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 툴바 초기화
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // 매물 RecyclerView 및 HouseListAdapter 초기화
        RecyclerView recyclerView = findViewById(R.id.main_recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter.setOnItemClickListener(new OnHouseItemClickListener() {
            @Override
            public void onItemClick(HouseListAdapter.ViewHolder viewHolder, View view, int position) {
                String address = adapter.getItem(position).getAddress();

                Intent intent = new Intent(MainActivity.this, HouseDetailActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(HouseListAdapter.ViewHolder viewHolder, View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);

        // 탭 메뉴 아래 가려진 매물 눌림 방지
        LinearLayout hidden_layout = findViewById(R.id.main_hidden_layout);
        hidden_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        // TabLayout 초기화
        setTabLayout();

        // 토글 버튼 그룹 리스트 초기화
        setToggleButtonGroups();

        // 적용, 초기화 버튼 초기화
        setButtons(tabLayout);

        // NavigationView 초기화
        setNavigationView();

        // 첫번째 탭 메뉴 초기화
        FlowLayout layout = findViewById(R.id.main_layout_toggleButtons);
        ArrayList<ToggleButton> list = toggleButtonGroups.get(0).getToggleButtons();
        for (ToggleButton button : list) {
            layout.addView(button);
        }

        // 매물 리스트 초기화 및 HouseListAdapter에 리스트 등록
        items.clear();

        House.SerializedData serializedData = new House.SerializedData();
        serializedData.uid = 0;
        serializedData.address = "대구광역시 달성군 다사읍 달구벌대로174길 10-17";
        serializedData.state = 0;
        serializedData.price = "5억 4000";
        serializedData.paymentType = 1;
        serializedData.houseType = 1;

        for (int i = 0; i < 12; i++) {
            items.add(new House(serializedData));
        }
        adapter.setItems(items);
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
                hideHiddenLayout();
                intent = new Intent(this, HouseModifyActivity.class);
                startActivity(intent);
                break;

            case R.id.main_menu_my_menu:
                hideHiddenLayout();
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

        if (getHiddenLayoutVisibility() == View.VISIBLE) {
            hideHiddenLayout();
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
        if (requestCode == FILTER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setCheckedStates();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void rearrangeList(String keyword) {
        ArrayList<House> temp = new ArrayList<>();
        for (House i : items) {
            if ((keyword == null || i.getHouseNumber().contains(keyword))
                    && Filter.isMatch(i)) {
                temp.add(i);
            }
        }

        adapter.setItems(temp);
        adapter.notifyDataSetChanged();
    }

    private void setTabLayout() {
        // TabLayout 초기화
        tabLayout = findViewById(R.id.main_tabLayout);

        for (String tabTitle : tabTitles) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(tabTitle);
            tabLayout.addTab(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showHiddenLayout();

                LinearLayout deposit_layout = findViewById(R.id.main_deposit_layout);
                LinearLayout monthly_rent_layout = findViewById(R.id.main_monthly_rent_layout);
                deposit_layout.setVisibility(View.GONE);
                monthly_rent_layout.setVisibility(View.GONE);

                FlowLayout flowLayout = findViewById(R.id.main_layout_toggleButtons);
                flowLayout.removeAllViews();

                if (tab.getText().toString().equals("보증금")) {
                    deposit_layout.setVisibility(View.VISIBLE);

                    TextView textView = findViewById(R.id.deposit_textView);
                    EditText max = findViewById(R.id.deposit_editText_max);
                    EditText min = findViewById(R.id.deposit_editText_min);

                    textView.setText(Filter.getDepositMin() + "원 ~ " + Filter.getDepositMax() + "원");
                    max.setText(String.valueOf(Filter.getDepositMax()));
                    min.setText(String.valueOf(Filter.getDepositMin()));

                } else if (tab.getText().toString().equals("월세금")) {
                    monthly_rent_layout.setVisibility(View.VISIBLE);

                    TextView textView = findViewById(R.id.monthly_rent_textView);
                    EditText max = findViewById(R.id.monthly_rent_editText_max);
                    EditText min = findViewById(R.id.monthly_rent_editText_min);
                    CheckBox checkBox = findViewById(R.id.monthly_rent_checkBox);

                    textView.setText(Filter.getMonthlyRentMin() + "원 ~ " + Filter.getMonthlyRentMax() + "원");
                    max.setText(String.valueOf(Filter.getMonthlyRentMax()));
                    min.setText(String.valueOf(Filter.getMonthlyRentMin()));
                    checkBox.setChecked(Filter.isIsContainManageFee());

                } else {
                    toggleButtonGroups.get(tab.getPosition()).loadCheckedStates();
                    ArrayList<ToggleButton> list = toggleButtonGroups.get(tab.getPosition()).getToggleButtons();
                    for (ToggleButton button : list) {
                        flowLayout.addView(button);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (getHiddenLayoutVisibility() == View.VISIBLE) {
                    hideHiddenLayout();

                } else {
                    showHiddenLayout();
                }
            }
        });

        ImageButton filter = findViewById(R.id.main_imageButton);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHiddenLayout();
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivityForResult(intent, FILTER_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private void setToggleButtonGroups() {
        for (int i = 0; i < tabTitles.length; i++) {
            if (toggleButtonTitles[i] == null) {
                toggleButtonGroups.add(null);
                continue;
            }

            ToggleButtonGroup toggleButtonGroup = new ToggleButtonGroup(this, tabTitles[i]);
            toggleButtonGroup.addToggleButtons(toggleButtonTitles[i]);
            toggleButtonGroups.add(toggleButtonGroup);
        }
    }

    private void setButtons(TabLayout tabLayout) {
        Button button_apply = findViewById(R.id.main_button_apply);
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tabPos = tabLayout.getSelectedTabPosition();
                String tabPageTitle = tabLayout.getTabAt(tabPos).getText().toString();

                if (tabPageTitle.equals("보증금")) {
                    EditText max = findViewById(R.id.deposit_editText_max);
                    EditText min = findViewById(R.id.deposit_editText_min);

                    String minStr = min.getText().toString();
                    String maxStr = max.getText().toString();

                    int minValue = minStr.equals("") ? 0 : Integer.parseInt(minStr);
                    int maxValue = maxStr.equals("") ? Integer.MAX_VALUE : Integer.parseInt(maxStr);
                    Filter.setDeposit(minValue, maxValue);

                } else if (tabPageTitle.equals("월세금")) {
                    EditText max = findViewById(R.id.monthly_rent_editText_max);
                    EditText min = findViewById(R.id.monthly_rent_editText_min);
                    CheckBox checkBox = findViewById(R.id.monthly_rent_checkBox);

                    String minStr = min.getText().toString();
                    String maxStr = max.getText().toString();

                    int minValue = minStr.equals("") ? 0 : Integer.parseInt(minStr);
                    int maxValue = maxStr.equals("") ? Integer.MAX_VALUE : Integer.parseInt(maxStr);
                    boolean isContain = checkBox.isChecked();
                    Filter.setMonthlyRen(isContain, minValue, maxValue);

                } else {
                    toggleButtonGroups.get(tabPos).saveCheckedStates();
                    HashMap<String, Boolean> list = toggleButtonGroups.get(tabPos).getToggleButtonCheckedStates();
                    Filter.addCheckedStates(toggleButtonGroups.get(tabPos).getTitle(), list);
                }

                LinearLayout linearLayout = findViewById(R.id.main_hidden_layout);
                linearLayout.setVisibility(View.GONE);
                rearrangeList(null);
            }
        });

        Button button_reset = findViewById(R.id.main_button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tabPos = tabLayout.getSelectedTabPosition();
                String tabPageTitle = tabLayout.getTabAt(tabPos).getText().toString();

                if (tabPageTitle.equals("보증금")) {
                    TextView textView = findViewById(R.id.deposit_textView);
                    EditText max = findViewById(R.id.deposit_editText_max);
                    EditText min = findViewById(R.id.deposit_editText_min);

                    textView.setText("0원 ~ 전체");
                    max.setText("");
                    min.setText("");
                    Filter.setDeposit(0, Integer.MAX_VALUE);

                } else if (tabPageTitle.equals("월세금")) {
                    TextView textView = findViewById(R.id.monthly_rent_textView);
                    EditText max = findViewById(R.id.monthly_rent_editText_max);
                    EditText min = findViewById(R.id.monthly_rent_editText_min);
                    CheckBox checkBox = findViewById(R.id.monthly_rent_checkBox);

                    textView.setText("0원 ~ 전체");
                    max.setText("");
                    min.setText("");
                    checkBox.setChecked(false);
                    Filter.setMonthlyRen(false, 0, Integer.MAX_VALUE);

                } else {
                    toggleButtonGroups.get(tabPos).resetCheckedStates();
                    HashMap<String, Boolean> list = toggleButtonGroups.get(tabPos).getToggleButtonCheckedStates();
                    Filter.addCheckedStates(toggleButtonGroups.get(tabPos).getTitle(), list);
                    rearrangeList(null);
                }
            }
        });
    }

    private void setNavigationView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.lockSwipe(GravityCompat.END);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                if (id == R.id.nav_menu_logout) {
                    SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                return true;
            }
        });
    }

    private void setCheckedStates() {
        for (ToggleButtonGroup group : toggleButtonGroups) {
            if (group != null) {
                group.initCheckedStates(Filter.getCheckedStates(group.getTitle()));
            }
        }

        rearrangeList(null);
    }

    private int getHiddenLayoutVisibility() {
        LinearLayout linearLayout = findViewById(R.id.main_hidden_layout);
        return linearLayout.getVisibility();
    }

    private void showHiddenLayout() {
        LinearLayout linearLayout = findViewById(R.id.main_hidden_layout);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void hideHiddenLayout() {
        LinearLayout linearLayout = findViewById(R.id.main_hidden_layout);
        linearLayout.setVisibility(View.GONE);

        Button button_reset = findViewById(R.id.main_button_reset);
        button_reset.performClick();
    }
}