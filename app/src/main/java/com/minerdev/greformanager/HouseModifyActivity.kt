package com.minerdev.greformanager

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.Request
import com.minerdev.greformanager.HttpConnection.OnReceiveListener
import com.minerdev.greformanager.databinding.ActivityHouseModifyBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

class HouseModifyActivity : AppCompatActivity() {
    private val adapter = SectionPageAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
    private val onPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> binding.houseModifyButtonPrevious.isEnabled = false
                1, 2 -> {
                    binding.houseModifyButtonPrevious.isEnabled = true
                    binding.houseModifyButtonNext.visibility = View.VISIBLE
                    binding.houseModifyButtonSave.visibility = View.GONE
                }
                3 -> {
                    binding.houseModifyButtonNext.visibility = View.GONE
                    binding.houseModifyButtonSave.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
    private val viewModel: HouseModifyViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    private val binding by lazy { ActivityHouseModifyBinding.inflate(layoutInflater) }

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        mode = intent.getStringExtra("mode")

        if (mode == "add") {
            actionBar?.title = "매물 추가"

        } else if (mode == "modify") {
            actionBar?.title = "매물 정보 수정"

            val house: House = intent.getParcelableExtra("house_value")!!
            viewModel.house = house
            imageViewModel.getOrderByPosition(viewModel.house.id).observe(this, { images: List<Image> ->
                val uris = ArrayList<Uri>()
                for (image in images) {
                    uris.add(Uri.parse(Constants.instance.DNS + "/storage/images/" + image.house_id + "/" + image.title))
                    if (image.thumbnail.toInt() == 1) {
                        viewModel.thumbnail = image.position.toInt()
                    }
                }

                viewModel.images = images as ArrayList<Image>
                viewModel.imageUris.addAll(uris)
            })
        }

        binding.houseModifyButtonNext.setOnClickListener { toNextPage() }
        binding.houseModifyButtonPrevious.isEnabled = false
        binding.houseModifyButtonPrevious.setOnClickListener {
            hideKeyboard()
            val current = binding.houseModifyViewPager.currentItem
            val listener = adapter.getItem(current) as OnSaveDataListener
            listener.saveData()
            binding.houseModifyViewPager.currentItem = binding.houseModifyViewPager.currentItem - 1
        }
        binding.houseModifyButtonSave.visibility = View.GONE
        binding.houseModifyButtonSave.setOnClickListener { askSave() }
        setViewPager()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("저장하지 않고 돌아가시겠습니까?")
        builder.setIcon(R.drawable.ic_round_help_24)
        builder.setPositiveButton("확인") { _: DialogInterface?, _: Int -> super@HouseModifyActivity.finish() }
        builder.setNegativeButton("취소") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        adapter.getItem(binding.houseModifyViewPager.currentItem).onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setViewPager() {
        binding.houseModifyViewPager.addOnPageChangeListener(onPageChangeListener)
        adapter.addFragment(InfoFragment1(), "매물 정보 입력")
        adapter.addFragment(InfoFragment2(), "매물 정보 입력")
        adapter.addFragment(InfoFragment3(), "매물 정보 입력")
        adapter.addFragment(ImageFragment(), "매물 사진 선택")
        binding.houseModifyViewPager.adapter = adapter
    }

    private fun hideKeyboard() {
        if (currentFocus != null) {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun toNextPage() {
        hideKeyboard()
        val current = binding.houseModifyViewPager.currentItem
        val listener = adapter.getItem(current) as OnSaveDataListener
        if (listener.checkData()) {
            listener.saveData()
            binding.houseModifyViewPager.currentItem = current + 1
        } else {
            Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askSave() {
        hideKeyboard()
        val current = binding.houseModifyViewPager.currentItem
        val listener = adapter.getItem(current) as OnSaveDataListener
        if (listener.checkData()) {
            listener.saveData()

            val builder = AlertDialog.Builder(this)
            builder.setMessage("저장하시겠습니까?")
            builder.setIcon(R.drawable.ic_round_help_24)
            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                viewModel.house.thumbnail = viewModel.thumbnailTitle

                count = 0

                if (mode == "add") {
                    add()

                } else if (mode == "modify") {
                    modify()
                }
            }
            builder.setNegativeButton("취소") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()

        } else {
            Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun add() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("데이터 전송중...")
        progressDialog.setCancelable(false)

        HttpConnection.instance.send(this, Request.Method.POST,
                "houses", viewModel.house, object : OnReceiveListener {
            override fun onReceive(receivedData: String) {
                val data = Json.decodeFromString<House>(receivedData)
                viewModel.house.id = data.id
                viewModel.house.created_at = data.created_at
                viewModel.house.updated_at = data.updated_at

                HttpConnection.instance.send(application, Request.Method.POST,
                        "houses/" + data.id + "/images", viewModel.imageUris, viewModel.images, object : OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val imageData = Json.decodeFromString<Image>(receivedData)
                        imageViewModel.insert(imageData)
                        Log.d("DB_INSERT", receivedData)

                        count++

                        if (count == viewModel.imageUris.size) {
                            progressDialog.dismiss()
                            val intent = Intent()
                            intent.putExtra("house_value", viewModel.house)
                            setResult(RESULT_OK, intent)
                            super@HouseModifyActivity.finish()
                        }
                    }
                })

                progressDialog.show()
            }
        })
    }

    private fun modify() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("데이터 전송중...")
        progressDialog.setCancelable(false)

        val id = viewModel.house.id
        HttpConnection.instance.send(this, Request.Method.PATCH,
                "houses/$id", viewModel.house, object : OnReceiveListener {
            override fun onReceive(receivedData: String) {
                val data = Json.decodeFromString<House>(receivedData)
                viewModel.house.updated_at = data.updated_at

                imageViewModel.deleteAll(id)

                HttpConnection.instance.send(application, Request.Method.POST,
                        "houses/$id/images", viewModel.imageUris, viewModel.images, object : OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val imageData = Json.decodeFromString<Image>(receivedData)
                        imageViewModel.insert(imageData)
                        Log.d("DB_INSERT", receivedData)

                        count++

                        if (count == viewModel.imageUris.size) {
                            progressDialog.dismiss()
                            val intent = Intent()
                            intent.putExtra("house_value", viewModel.house)
                            setResult(RESULT_OK, intent)
                            super@HouseModifyActivity.finish()
                        }
                    }
                })

                progressDialog.show()
            }
        })
    }

    companion object {
        private var mode: String? = null
    }
}