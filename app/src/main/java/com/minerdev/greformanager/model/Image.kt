package com.minerdev.greformanager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Image(
        var id: Int = 0,
        var createdAt: String = "",
        var updatedAt: String = "",
        var url: String = "",
        var position: Byte = 0,
        var thumbnail: Byte = 0,
        var house_id: Int = 0,
        var localUri: String? = null,
        var state: Int = 0
) : Parcelable
