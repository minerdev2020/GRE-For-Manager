package com.minerdev.greformanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.minerdev.greformanager.R
import com.minerdev.greformanager.custom.view.ToggleButtonGroup
import com.minerdev.greformanager.custom.adapter.ImageSliderAdapter
import com.minerdev.greformanager.databinding.ActivityHouseDetailBinding
import com.minerdev.greformanager.http.geocode.Geocode
import com.minerdev.greformanager.http.geocode.Geocode.OnDataReceiveListener
import com.minerdev.greformanager.http.geocode.GeocodeResult
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.model.wrapper.HouseWrapper
import com.minerdev.greformanager.viewmodel.HouseDetailViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker

class HouseDetailActivity : AppCompatActivity() {
    private val adapter = ImageSliderAdapter()
    private val viewModel: HouseDetailViewModel by viewModels()

    private lateinit var binding: ActivityHouseDetailBinding
    private var originalState: Byte = 0
    private var houseId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_detail)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val intent = intent
        houseId = intent.getIntExtra("house_id", 0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.house.observe(this, { house ->
            originalState = house.state
            viewModel.houseWrapper.value = HouseWrapper(house)
            refreshUI(house)
        })

        viewModel.images.observe(this, { images: List<Image> ->
            adapter.addImages(images)
            adapter.notifyDataSetChanged()
        })

        binding.viewPager.adapter = adapter
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
                intent.putExtra("house_value", viewModel.house.value)
                startActivity(intent)
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.viewPager.currentItem = 0
        viewModel.loadHouse(houseId)
        viewModel.loadImages(houseId)
    }

    override fun finish() {
        viewModel.house.value?.let {
            if (originalState != it.state) {
                viewModel.modifyHouseState(it.id, it.state)
            }
        }

        super.finish()
    }

    private fun refreshUI(house: House) {
        setMapFragment(house)

        binding.invalidateAll()
        binding.flowLayoutManageFee.removeAllViews()
        binding.flowLayoutOptions.removeAllViews()

        val toggleButtonGroupManageFee = ToggleButtonGroup(this, "ManageFee")
        toggleButtonGroupManageFee.addToggleButtonsFromText(house.manage_fee_contains)
        for (toggleButton in toggleButtonGroupManageFee.toggleButtons) {
            binding.flowLayoutManageFee.addView(toggleButton)
        }

        val toggleButtonGroupOptions = ToggleButtonGroup(this, "Options")
        toggleButtonGroupOptions.addToggleButtonsFromText(house.options)
        for (toggleButton in toggleButtonGroupOptions.toggleButtons) {
            binding.flowLayoutOptions.addView(toggleButton)
        }
    }

    private fun setMapFragment(house: House) {
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

            Geocode.getQueryResponseFromNaver(house.address.substring(8), object : OnDataReceiveListener {
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
}