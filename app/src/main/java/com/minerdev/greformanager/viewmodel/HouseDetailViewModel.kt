package com.minerdev.greformanager.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minerdev.greformanager.Repository
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.model.wrapper.HouseWrapper

class HouseDetailViewModel : ViewModel() {
    val house: MutableLiveData<House>
    val images: MutableLiveData<List<Image>>

    // 데이터 바인딩을 위해 공개함
    val houseWrapper = MutableLiveData<HouseWrapper>()

    private val repository: Repository = Repository()

    init {
        house = repository.house
        images = repository.allImages
        houseWrapper.value = house.value?.let { HouseWrapper(it) }
    }

    fun loadHouse(houseId: Int) {
        repository.loadHouse(houseId)
    }

    fun loadImages(houseId: Int) {
        repository.loadImages(houseId)
    }

    fun modifyHouseState(id: Int, state: Byte) {
        repository.modifyHouseState(id, state)
    }

    companion object {
        fun convertBooleanToVisibility(visible: Boolean): Int {
            return if (visible) View.VISIBLE else View.GONE
        }
    }
}