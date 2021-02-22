package com.minerdev.greformanager

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class HouseModifyViewModel : ViewModel() {
    lateinit var house: House
    var imageUris = ArrayList<Uri>()
    var images = ArrayList<Image>()
    var thumbnail = 0
    var thumbnailTitle: String = ""
        private set

    fun saveImages(context: Context) {
        for ((position, uri) in imageUris.withIndex()) {
            val image = Image()
            val file = File(AppHelper.instance.getPathFromUri(context, uri))
            val fileName: String = file.name
            val fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1)

            image.title = System.currentTimeMillis().toString() + "." + fileExtension
            image.position = position.toByte()
            image.house_id = house.id

            if (position == thumbnail) {
                image.thumbnail = 1
                thumbnailTitle = image.title!!

            } else {
                image.thumbnail = 0
            }

            images.add(image)
        }
    }
}