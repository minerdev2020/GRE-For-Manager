package com.minerdev.greformanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ImageRepository = ImageRepository(application)

    fun getLastUpdatedAt(house_id: Int): Long? {
        return repository.getLastUpdatedAt(house_id)
    }

    fun getOrderByPosition(house_id: Int): LiveData<List<Image>> {
        return repository.getOrderByPosition(house_id)
    }

    fun insert(image: Image) {
        repository.insert(image)
    }

    fun insert(images: List<Image>) {
        repository.insert(images)
    }

    fun updateOrInsert(image: Image) {
       repository.updateOrInsert(image)
    }

    fun deleteAll(house_id: Int) {
        repository.deleteAll(house_id)
    }
}