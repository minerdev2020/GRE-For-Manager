package com.minerdev.greformanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.minerdev.greformanager.Repository
import com.minerdev.greformanager.model.House

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val allSale: LiveData<MutableList<House>>
    val allSold: LiveData<MutableList<House>>

    private val repository: Repository = Repository(application)

    init {
        allSale = repository.allSale
        allSold = repository.allSold
    }

    operator fun get(id: Int): House? {
        return repository[id]
    }

    fun loadHouses() {
        repository.loadHouses()
    }

    fun deleteAll() {
        repository.deleteAll()
    }
}