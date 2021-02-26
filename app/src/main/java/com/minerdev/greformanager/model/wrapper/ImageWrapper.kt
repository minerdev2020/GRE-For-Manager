package com.minerdev.greformanager.model.wrapper

import android.net.Uri
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.Constants.BASE_URL

class ImageWrapper(val data: Image) {
    val id: Int
        get() = data.id

    val createdAt: String
        get() = data.createdAt

    val updatedAt: String
        get() = data.updatedAt

    val url: String
        get() = data.url

    val position: Byte
        get() = data.position

    val thumbnail: Byte
        get() = data.thumbnail

    val houseId: Int
        get() = data.house_id

    fun getUri(position: Int): Uri {
        return Uri.parse(BASE_URL + data.url)
    }

}