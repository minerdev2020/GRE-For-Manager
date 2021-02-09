package com.minerdev.greformanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private EditText editText_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_pw = findViewById(R.id.login_editText_pw);

        setupButtons();
        setupEditTexts();

        if (checkLoginStatus()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            finish();

        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void tryLogin(View view, String id, String pw) {
        if (!id.isEmpty() && !pw.isEmpty()) {
            if (id.equals("root") && pw.equals("admin")) {
                SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("id", id);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Snackbar.make(view, "한 번 더 누르시면 종료됩니다.", Snackbar.LENGTH_LONG).show();
                editText_pw.setText("");
            }

        } else {
            Snackbar.make(view, "아이디나 패스워드가 비어있습니다!", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE);
        return sharedPreferences != null && sharedPreferences.contains("id");
    }

    private void setupButtons() {
        Button button_login = findViewById(R.id.login_button_login);
        button_login.setOnClickListener(view -> {
            EditText editText_id = findViewById(R.id.login_editText_id);
            EditText editText_pw = findViewById(R.id.login_editText_pw);
            String user_id = editText_id.getText().toString();

            tryLogin(view, user_id, editText_pw.getText().toString());
        });
    }

    private void setupEditTexts() {
        TextInputEditText editText_id = findViewById(R.id.login_editText_id);
        editText_id.setFilters(new InputFilter[]{(charSequence, i, i1, spanned, i2, i3) -> {
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
            if (charSequence.equals("") || pattern.matcher(charSequence).matches()) {
                return charSequence;
            }

            return "";
        }, new InputFilter.LengthFilter(8)});

        TextInputEditText editText_pw = findViewById(R.id.login_editText_pw);
        editText_pw.setFilters(new InputFilter[]{(charSequence, i, i1, spanned, i2, i3) -> {
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
            if (charSequence.equals("") || pattern.matcher(charSequence).matches()) {
                return charSequence;
            }

            return "";
        }, new InputFilter.LengthFilter(8)});

        editText_pw.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                Button button_login = findViewById(R.id.login_button_login);
                button_login.performClick();

                return true;
            }

            return false;
        });
    }
}