package com.minerdev.greformanager.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spanned
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.minerdev.greformanager.databinding.ActivityLoginBinding
import com.minerdev.greformanager.utils.Constants.FINISH_INTERVAL_TIME
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupButtons()
        setupEditTexts()

        if (checkLoginStatus()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            finish()

        } else {
            backPressedTime = tempTime
            Toast.makeText(this, "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tryLogin(view: View, id: String, pw: String) {
        if (id.isNotEmpty() && pw.isNotEmpty()) {
            if (id == "root" && pw == "admin") {
                val sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.putString("id", id)
                editor.apply()

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Snackbar.make(view, "아이디나 패스워드가 틀렸습니다!", Snackbar.LENGTH_LONG).show()
                binding.textInputEtPw.setText("")
            }

        } else {
            Snackbar.make(view, "아이디나 패스워드가 비어있습니다!", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun checkLoginStatus(): Boolean {
        val sharedPreferences = getSharedPreferences("login", Activity.MODE_PRIVATE)
        return sharedPreferences != null && sharedPreferences.contains("id")
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            tryLogin(it, binding.textInputEtId.text.toString(), binding.textInputEtPw.text.toString())
        }
    }

    private fun setupEditTexts() {
        binding.textInputEtId.filters = arrayOf(InputFilter { source: CharSequence, _: Int, _: Int, _: Spanned?, _: Int, _: Int ->
            val pattern = Pattern.compile("^[a-zA-Z0-9]*$")
            if (source == "" || pattern.matcher(source).matches()) {
                return@InputFilter source
            }
            ""
        }, LengthFilter(8))

        binding.textInputEtPw.filters = arrayOf(InputFilter { source: CharSequence, _: Int, _: Int, _: Spanned?, _: Int, _: Int ->
            val pattern = Pattern.compile("^[a-zA-Z0-9]*$")
            if (source == "" || pattern.matcher(source).matches()) {
                return@InputFilter source
            }
            ""
        }, LengthFilter(8))

        binding.textInputEtPw.setOnEditorActionListener { _: TextView?, i: Int, keyEvent: KeyEvent? ->
            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                binding.btnLogin.performClick()

                return@setOnEditorActionListener true
            }
            false
        }
    }
}