package com.minerdev.greformanager

import android.net.Uri
import androidx.lifecycle.ViewModel
import java.util.*

class SharedViewModel : ViewModel() {
    var house = House()
    var imageUris = ArrayList<Uri>()
    var images = ArrayList<Image>()
    var thumbnail = 0
    var thumbnailTitle: String = ""
        private set

    fun saveImages() {
        for (image in images) {
            image.house_id = house.id
            if (image.position == thumbnail.toByte()) {
                thumbnailTitle = image.title
            }
        }
    }
}