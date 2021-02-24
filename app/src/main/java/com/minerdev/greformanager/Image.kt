package com.minerdev.greformanager

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity
@Parcelize
@Serializable
data class Image(
        @PrimaryKey var id: Int = 0,
        var createdAt: String = "",
        var updatedAt: String = "",
        var title: String = "",
        var path: String = "",
        var position: Byte = 0,
        var thumbnail: Byte = 0,
        var house_id: Int = 0
) : Parcelable
