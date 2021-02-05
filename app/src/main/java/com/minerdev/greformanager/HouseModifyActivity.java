package com.minerdev.greformanager;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.viewpager.widget.ViewPager;

public class HouseModifyActivity extends AppCompatActivity {
    private final SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager(),
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    private NonSwipeViewPager viewPager;
    private Button button_next;
    private Button button_previous;
    private Button button_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_modify);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        if (mode.equals("add")) {
            actionBar.setTitle("매물 추가");

        } else if (mode.equals("modify")) {
            actionBar.setTitle("매물 정보 수정");
        }

        button_next = findViewById(R.id.house_modify_button_next);
        button_next.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            int current = viewPager.getCurrentItem();
            OnSaveDataListener listener = (OnSaveDataListener) adapter.getItem(current);
            if (listener.checkData()) {
                listener.saveData();
                viewPager.setCurrentItem(current + 1);

            } else {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });

        button_previous = findViewById(R.id.house_modify_button_previous);
        button_previous.setEnabled(false);
        button_previous.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        });

        button_save = findViewById(R.id.house_modify_button_save);
        button_save.setVisibility(View.GONE);
        button_save.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            askSave();
        });

        setViewPager();
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
        builder.setPositiveButton("확인", (dialog, which) -> HouseModifyActivity.super.finish());

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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        button_previous.setEnabled(true);
                        button_next.setVisibility(View.VISIBLE);
                        button_save.setVisibility(View.GONE);
                        break;

                    case 2:
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
        });

        adapter.addFragment(new InfoFragment(), "매물 정보 입력");
        adapter.addFragment(new ImageFragment(), "매물 사진 선택");
        adapter.addFragment(new PreviewFragment(), "미리보기");

        viewPager.setAdapter(adapter);
    }

    private void askSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("저장하시겠습니까?");
        builder.setIcon(R.drawable.ic_round_help_24);
        builder.setPositiveButton("확인", (dialog, which) -> {
            SendData.getInstance().start(this);
            HouseModifyActivity.super.finish();
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}