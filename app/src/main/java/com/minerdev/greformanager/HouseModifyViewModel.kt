package com.minerdev.greformanager

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import org.json.JSONObject

class HouseModifyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(application)

    fun getOrderByPosition(houseId: Int): LiveData<List<Image>> {
        return repository.getOrderByPosition(houseId)
    }

    fun loadImages(houseId: Int) {
        repository.loadImages(houseId)
    }

    fun add(house: House, imageUris: List<Uri>, images: List<Image>) {
        repository.add(house, imageUris, images)
    }

    fun modify(id: Int, data: JSONObject) {
        repository.modify(id, data)
    }

    fun modify(house: House, imageUris: List<Uri>, images: List<Image>) {
        repository.modify(house, imageUris, images)
    }

    fun deleteAll(house_id: Int) {
        repository.deleteAll(house_id)
    }
}