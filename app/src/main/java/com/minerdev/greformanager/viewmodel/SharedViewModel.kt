package com.minerdev.greformanager.viewmodel

import androidx.lifecycle.ViewModel
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import java.util.*

class SharedViewModel : ViewModel() {
    var house = House()
    var thumbnail = 0

    lateinit var images: ArrayList<Image>
    lateinit var deletedImages: ArrayList<Image>
}