package com.minerdev.greformanager

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class HouseModifyViewModel : ViewModel() {
    var house: House? = null
        private set
    var imageUris: MutableList<Uri>? = null
    var images: MutableList<Image>? = null
    var thumbnail = 0
    var thumbnailTitle: String? = null
        private set

    fun setMode(mode: String, house: House?) {
        if (mode == "add") {
            this.house = null
            imageUris = ArrayList()

        } else if (mode == "modify" && house != null) {
            this.house = house
            imageUris = ArrayList()

        } else {
            this.house = null
            imageUris = null
        }
    }

    val newHouse: House
        get() {
            house = House()
            return house!!
        }

    fun saveImages(context: Context) {
        images = ArrayList()

        for ((position, uri) in imageUris!!.withIndex()) {
            val image = Image()
            val file = File(AppHelper.instance.getPathFromUri(context, uri))
            val fileName: String = file.name
            val fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1)

            image.title = System.currentTimeMillis().toString() + "." + fileExtension
            image.position = position.toByte()
            image.house_id = house!!.id

            if (position == thumbnail) {
                image.thumbnail = 1
                thumbnailTitle = image.title

            } else {
                image.thumbnail = 0
            }

            images?.add(image)
        }
    }
}