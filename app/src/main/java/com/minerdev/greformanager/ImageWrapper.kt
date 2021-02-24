package com.minerdev.greformanager

import android.net.Uri

class ImageWrapper(val data: Image) {
    val id: Int
        get() = data.id

    val createdAt: String
        get() = data.createdAt

    val updatedAt: String
        get() = data.updatedAt

    val title: String?
        get() = data.title

    val path: String?
        get() = data.path

    val position: Byte
        get() = data.position

    val thumbnail: Byte
        get() = data.thumbnail

    val houseId: Int
        get() = data.house_id

    fun getUri(position: Int): Uri {
        return Uri.parse(Constants.instance.DNS + "/storage/images/" + data.house_id + "/" + data.title)
    }

}