package com.minerdev.greformanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class HouseModifyActivity extends AppCompatActivity {
    private final SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

    private NonSwipeViewPager viewPager;
    private Button button_next;
    private Button button_previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_modify);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        button_next = findViewById(R.id.house_modify_button_next);
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

                if (getCurrentFocus() != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        button_previous = findViewById(R.id.house_modify_button_previous);
        button_previous.setEnabled(false);
        button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

                if (getCurrentFocus() != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
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
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HouseModifyActivity.super.finish();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

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
                button_previous.setEnabled(true);
                button_next.setText("다음");
                button_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

                        if (getCurrentFocus() != null) {
                            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                });

                if (position == 0) {
                    button_previous.setEnabled(false);

                } else if (position == (adapter.getCount() - 1)) {
                    button_next.setText("완료");
                    button_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getCurrentFocus() != null) {
                                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }

                            save();
                        }
                    });
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

    private void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("저장하시겠습니까?");
        builder.setIcon(R.drawable.ic_round_help_24);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendData();
                HouseModifyActivity.super.finish();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendData() {

    }
}