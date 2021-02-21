package com.minerdev.greformanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingConversion
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.minerdev.greformanager.Geocode.OnDataReceiveListener
import com.minerdev.greformanager.HttpConnection.OnReceiveListener
import com.minerdev.greformanager.databinding.ActivityHouseDetailBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker

class HouseDetailActivity : AppCompatActivity() {
    private val adapter = ImageAdapter()
    private var originalState = 0

    private val imageViewModel by lazy {
        ViewModelProvider(this,
                AndroidViewModelFactory(application)).get(ImageViewModel::class.java)
    }
    private lateinit var binding: ActivityHouseDetailBinding
    private lateinit var houseWrapper: HouseWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_detail)
        binding.activity = this

        val intent = intent
        val data: House? = intent.getParcelableExtra("house_value")
        originalState = data!!.state.toInt()
        houseWrapper = HouseWrapper(data)

        setSupportActionBar(binding.houseDetailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageViewModel.getOrderByPosition(data.id)!!.observe(this, Observer { images: List<Image?>? ->
            adapter.addImages(images)
            adapter.notifyDataSetChanged()
        })

        binding.houseDetailViewPager2Image.adapter = adapter

        setMapFragment()

        val toggleButtonGroupManageFee = ToggleButtonGroup(this, "ManageFee")
        toggleButtonGroupManageFee.addToggleButtonsFromText(houseWrapper.manageFeeContains)
        for (toggleButton in toggleButtonGroupManageFee.toggleButtons) {
            binding.houseDetailFlowLayoutManageFee.addView(toggleButton)
        }

        val toggleButtonGroupOptions = ToggleButtonGroup(this, "Options")
        toggleButtonGroupOptions.addToggleButtonsFromText(houseWrapper.options)
        for (toggleButton in toggleButtonGroupOptions.toggleButtons) {
            binding.houseDetailFlowLayoutOptions.addView(toggleButton)
        }

        loadItems(data.id)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.house_detail_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

            R.id.house_detail_menu_modify -> {
                val intent = Intent(this, HouseModifyActivity::class.java)
                intent.putExtra("mode", "modify")
                intent.putExtra("house_value", houseWrapper.data)
                startActivityForResult(intent, Constants.instance.HOUSE_MODIFY_ACTIVITY_REQUEST_CODE)
            }

            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        if (originalState != houseWrapper.data.state.toInt()) {
            val data = JsonObject()
            data.addProperty("state", houseWrapper.data.state)
            HttpConnection.instance.send(this, Request.Method.PATCH,
                    "houses/" + houseWrapper.data.id, data, null)
        }

        val intent = Intent()
        intent.putExtra("house_value", houseWrapper.data)
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.instance.HOUSE_MODIFY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val house: House? = data.getParcelableExtra("house_value")
                    val houseWrapper = HouseWrapper(house!!)
                    this.houseWrapper = houseWrapper
                    refreshUI()
                }
            }
        }
    }

    private fun refreshUI() {
        binding.invalidateAll()
        binding.houseDetailFlowLayoutManageFee.removeAllViews()
        binding.houseDetailFlowLayoutOptions.removeAllViews()

        val toggleButtonGroupManageFee = ToggleButtonGroup(this, "ManageFee")
        toggleButtonGroupManageFee.addToggleButtons(houseWrapper.manageFeeContains.split("\\|"))
        for (toggleButton in toggleButtonGroupManageFee.toggleButtons) {
            binding.houseDetailFlowLayoutManageFee.addView(toggleButton)
        }

        val toggleButtonGroupOptions = ToggleButtonGroup(this, "Options")
        toggleButtonGroupOptions.addToggleButtons(houseWrapper.options.split("\\|"))
        for (toggleButton in toggleButtonGroupOptions.toggleButtons) {
            binding.houseDetailFlowLayoutOptions.addView(toggleButton)
        }

        setMapFragment()
    }

    private fun setMapFragment() {
        var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        }

        mapFragment?.getMapAsync { naverMap: NaverMap ->
            naverMap.isLiteModeEnabled = true

            val uiSettings = naverMap.uiSettings
            uiSettings.isZoomControlEnabled = false
            uiSettings.setAllGesturesEnabled(false)

            Geocode.instance.getQueryResponseFromNaver(this@HouseDetailActivity, houseWrapper.address.substring(8))

            Geocode.instance.setOnDataReceiveListener(object : OnDataReceiveListener {
                override fun parseData(result: GeocodeResult?) {
                    result?.let {
                        val address = result.addresses!![0]
                        val latLng = LatLng(address.y.toDouble(), address.x.toDouble())
                        naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(latLng, 16.0))

                        val marker = Marker(latLng)
                        marker.map = naverMap
                    }
                }
            })
        }
    }

    private fun loadItems(house_id: Int) {
        HttpConnection.instance.receive(this, "houses/$house_id/images/last-updated-at",
                object : OnReceiveListener {
                    override fun onReceive(receivedData: String?) {
                        if (receivedData.isNullOrEmpty()) {
                            loadItemsFromWeb(house_id)

                        } else {
                            checkUpdate(house_id, receivedData)
                        }
                    }
                })
    }

    private fun checkUpdate(house_id: Int, receivedData: String) {
        Thread {
            val serverTimestamp = receivedData.toLong()
            val clientTimestamp = imageViewModel.getLastUpdatedAt(house_id) ?: 0

            if (serverTimestamp > clientTimestamp) {
                loadItemsFromWeb(house_id)
            }
        }.start()
    }

    private fun loadItemsFromWeb(house_id: Int) {
        HttpConnection.instance.receive(this, "houses/$house_id/images",
                object : OnReceiveListener {
                    override fun onReceive(receivedData: String?) {
                        val gson = Gson()
                        val array = gson.fromJson(receivedData, Array<Image>::class.java)
                        if (array == null) {
                            Toast.makeText(this@HouseDetailActivity, "데이터 수신 실패.", Toast.LENGTH_SHORT).show()
                            return
                        }

                        Log.d("last_updated_at", receivedData ?: "null")
                        imageViewModel.insert(*array)
                    }
                })
    }

    companion object {
        @BindingConversion
        fun convertBooleanToVisibility(visible: Boolean): Int {
            return if (visible) View.VISIBLE else View.GONE
        }
    }
}