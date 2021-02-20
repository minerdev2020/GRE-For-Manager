package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.google.gson.Gson;

public class HouseModifyActivity extends AppCompatActivity {
    private static String mode;
    private final SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager(),
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    private ImageViewModel imageViewModel;
    private HouseModifyViewModel viewModel;
    private NonSwipeViewPager viewPager;
    private Button button_next;
    private Button button_previous;
    private Button button_save;
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    button_previous.setEnabled(false);
                    break;

                case 1:
                case 2:
                    button_previous.setEnabled(true);
                    button_next.setVisibility(View.VISIBLE);
                    button_save.setVisibility(View.GONE);
                    break;

                case 3:
                    button_next.setVisibility(View.GONE);
                    button_save.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_modify);

        imageViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ImageViewModel.class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        House house = null;

        if (mode.equals("add")) {
            actionBar.setTitle("매물 추가");

        } else if (mode.equals("modify")) {
            actionBar.setTitle("매물 정보 수정");
            house = intent.getParcelableExtra("house_value");
        }

        viewModel = new ViewModelProvider(this).get(HouseModifyViewModel.class);
        viewModel.setMode(mode, house);

        button_next = findViewById(R.id.house_modify_button_next);
        button_next.setOnClickListener(v -> toNextPage());

        button_previous = findViewById(R.id.house_modify_button_previous);
        button_previous.setEnabled(false);
        button_previous.setOnClickListener(v -> {
            hideKeyboard();
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        });

        button_save = findViewById(R.id.house_modify_button_save);
        button_save.setVisibility(View.GONE);
        button_save.setOnClickListener(v -> askSave());

        setViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("저장하지 않고 돌아가시겠습니까?");
        builder.setIcon(R.drawable.ic_round_help_24);
        builder.setPositiveButton("확인", (dialog, which) -> {
            HouseModifyActivity.super.finish();
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        adapter.getItem(viewPager.getCurrentItem()).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setViewPager() {
        viewPager = findViewById(R.id.house_modify_viewPager);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        adapter.addFragment(new InfoFragment1(), "매물 정보 입력");
        adapter.addFragment(new InfoFragment2(), "매물 정보 입력");
        adapter.addFragment(new InfoFragment3(), "매물 정보 입력");
        adapter.addFragment(new ImageFragment(), "매물 사진 선택");

        viewPager.setAdapter(adapter);
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void toNextPage() {
        hideKeyboard();

        int current = viewPager.getCurrentItem();
        OnSaveDataListener listener = (OnSaveDataListener) adapter.getItem(current);
        if (listener.checkData()) {
            listener.saveData();
            viewPager.setCurrentItem(current + 1);

        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    private void askSave() {
        hideKeyboard();

        int current = viewPager.getCurrentItem();
        OnSaveDataListener listener = (OnSaveDataListener) adapter.getItem(current);
        if (listener.checkData()) {
            listener.saveData();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("저장하시겠습니까?");
            builder.setIcon(R.drawable.ic_round_help_24);

            builder.setPositiveButton("확인", (dialog, which) -> {
                if (mode.equals("add")) {
                    add();

                } else if (mode.equals("modify")) {
                    modify();
                }
            });

            builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    private void add() {
        HttpConnection.getInstance().send(this, Request.Method.POST,
                "houses", viewModel.getHouse(), receivedData -> {
                    Gson gson = new Gson();
                    House data = gson.fromJson(receivedData, House.class);
                    viewModel.getHouse().id = data.id;
                    viewModel.getHouse().created_at = data.created_at;
                    viewModel.getHouse().updated_at = data.updated_at;

                    HttpConnection.getInstance().send(getApplication(), Request.Method.POST,
                            "houses/" + data.id + "/images", viewModel.getImageUris(), viewModel.getThumbnail(), receivedData1 -> {
                                Gson gson1 = new Gson();
                                Image imageData = gson1.fromJson(receivedData1, Image.class);
                                imageViewModel.insert(imageData);
                                Log.d("DB_INSERT", receivedData1);
                            });

                    Intent intent = new Intent();
                    intent.putExtra("house_value", viewModel.getHouse());
                    setResult(RESULT_OK, intent);
                    HouseModifyActivity.super.finish();
                });
    }

    private void modify() {
        int id = viewModel.getHouse().id;
        HttpConnection.getInstance().send(this, Request.Method.PATCH,
                "houses/" + id, viewModel.getHouse(), receivedData -> {
                    Gson gson = new Gson();
                    House data = gson.fromJson(receivedData, House.class);
                    viewModel.getHouse().updated_at = data.updated_at;

                    HttpConnection.getInstance().send(getApplication(), Request.Method.POST,
                            "houses/" + id + "/images", viewModel.getImageUris(), viewModel.getThumbnail(), receivedData1 -> {
                                Gson gson1 = new Gson();
                                Image imageData = gson1.fromJson(receivedData1, Image.class);
                                imageViewModel.updateOrInsert(imageData);
                                Log.d("DB_INSERT", receivedData1);
                            });

                    Intent intent = new Intent();
                    intent.putExtra("house_value", viewModel.getHouse());
                    setResult(RESULT_OK, intent);
                    HouseModifyActivity.super.finish();
                });
    }
}