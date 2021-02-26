package com.minerdev.greformanager.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.minerdev.greformanager.R
import com.minerdev.greformanager.custom.ToggleButtonGroup
import com.minerdev.greformanager.custom.adapter.ImageSliderAdapter
import com.minerdev.greformanager.databinding.ActivityHouseDetailBinding
import com.minerdev.greformanager.http.geocode.Geocode
import com.minerdev.greformanager.http.geocode.Geocode.OnDataReceiveListener
import com.minerdev.greformanager.http.geocode.GeocodeResult
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.model.wrapper.HouseWrapper
import com.minerdev.greformanager.utils.Constants.HOUSE_MODIFY_ACTIVITY_REQUEST_CODE
import com.minerdev.greformanager.viewmodel.HouseDetailViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker

class HouseDetailActivity : AppCompatActivity() {
    private val adapter = ImageSliderAdapter()
    private val viewModel: HouseDetailViewModel by viewModels()

    // 데이터 바인딩을 위해 공개함
    var houseWrapper = HouseWrapper()
    private lateinit var binding: ActivityHouseDetailBinding
    private var originalState: Byte = 0
    private var houseId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_detail)
        binding.activity = this

        val intent = intent
        houseId = intent.getIntExtra("house_id", 0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.house.observe(this, { house ->
            originalState = house.state
            houseWrapper = HouseWrapper(house)
            initialize()
        })

        viewModel.images.observe(this, { images: List<Image> ->
            adapter.addImages(images)
            adapter.notifyDataSetChanged()
        })

        binding.viewPager2.adapter = adapter
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
                startActivityForResult(intent, HOUSE_MODIFY_ACTIVITY_REQUEST_CODE)
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHouse(houseId)
        viewModel.loadImages(houseId)
    }

    override fun finish() {
        if (originalState != houseWrapper.data.state) {
            viewModel.modifyHouseState(houseWrapper.data.id, houseWrapper.data.state)
        }

        super.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HOUSE_MODIFY_ACTIVITY_REQUEST_CODE) {
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

    private fun initialize() {
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

            Geocode.getQueryResponseFromNaver(houseWrapper.address.substring(8), object : OnDataReceiveListener {
                override fun parseData(result: GeocodeResult?) {
                    val addresses = result?.addresses ?: return
                    val latLng = LatLng(addresses[0].y.toDouble(), addresses[0].x.toDouble())
                    naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(latLng, 16.0))

                    val marker = Marker(latLng)
                    marker.map = naverMap
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