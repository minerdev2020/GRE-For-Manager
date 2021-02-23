package com.minerdev.greformanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.google.gson.JsonObject
import com.minerdev.greformanager.Geocode.OnDataReceiveListener
import com.minerdev.greformanager.databinding.ActivityHouseDetailBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class HouseDetailActivity : AppCompatActivity() {
    lateinit var houseWrapper: HouseWrapper

    private val adapter = ImageSliderAdapter()
    private val viewModel: HouseModifyViewModel by viewModels()

    private lateinit var binding: ActivityHouseDetailBinding
    private var originalState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_detail)
        binding.activity = this

        val intent = intent
        val data: House? = intent.getParcelableExtra("house_value")
        originalState = data!!.state.toInt()
        houseWrapper = HouseWrapper(data)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getOrderByPosition(data.id).observe(this, { images: List<Image> ->
            adapter.addImages(images)
            adapter.notifyDataSetChanged()
        })

        binding.viewPager2.adapter = adapter

        setMapFragment()

        val toggleButtonGroupManageFee = ToggleButtonGroup(this, "ManageFee")
        toggleButtonGroupManageFee.addToggleButtonsFromText(houseWrapper.manageFeeContains)
        for (toggleButton in toggleButtonGroupManageFee.toggleButtons) {
            binding.flowLayoutManageFee.addView(toggleButton)
        }

        val toggleButtonGroupOptions = ToggleButtonGroup(this, "Options")
        toggleButtonGroupOptions.addToggleButtonsFromText(houseWrapper.options)
        for (toggleButton in toggleButtonGroupOptions.toggleButtons) {
            binding.flowLayoutOptions.addView(toggleButton)
        }

        viewModel.loadImages(data.id)
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
            HttpConnection.instance.send(Request.Method.PATCH,
                    "houses/" + houseWrapper.data.id, data, object : HttpConnection.OnReceiveListener {
                override fun onReceive(receivedData: String) {
                    val house = Json.decodeFromString<House>(receivedData)
                    viewModel.modify(house)
                }
            })
        }

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
        binding.flowLayoutManageFee.removeAllViews()
        binding.flowLayoutOptions.removeAllViews()

        val toggleButtonGroupManageFee = ToggleButtonGroup(this, "ManageFee")
        toggleButtonGroupManageFee.addToggleButtons(houseWrapper.manageFeeContains.split('|'))
        for (toggleButton in toggleButtonGroupManageFee.toggleButtons) {
            binding.flowLayoutManageFee.addView(toggleButton)
        }

        val toggleButtonGroupOptions = ToggleButtonGroup(this, "Options")
        toggleButtonGroupOptions.addToggleButtons(houseWrapper.options.split('|'))
        for (toggleButton in toggleButtonGroupOptions.toggleButtons) {
            binding.flowLayoutOptions.addView(toggleButton)
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

    companion object {
        fun convertBooleanToVisibility(visible: Boolean): Int {
            return if (visible) View.VISIBLE else View.GONE
        }
    }
}