package com.minerdev.greformanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity
@Parcelize
@Serializable
data class House(
        @PrimaryKey var id: Int = 0,
        var createdAt: String = "",
        var updatedAt: String = "",
        var address: String = "",
        var number: String = "",
        var house_type: Byte = 0,
        var facility: Byte = 0,
        var payment_type: Byte = 0,
        var price: Int = 0,
        var deposit: Int = 0,
        var monthly_rent: Int = 0,
        var premium: Int = 0,
        var manage_fee: Int = 0,
        var manage_fee_contains: String = "",
        var area_meter: Float = 0f,
        var rent_area_meter: Float = 0f,
        var building_floor: Byte = 0,
        var floor: Byte = 0,
        var structure: Byte = 0,
        var bathroom: Byte = 0,
        var bathroom_location: Byte = 0,
        var direction: Byte = 0,
        var built_date: String = "",
        var move_date: String = "",
        var options: String = "",
        var detail_info: String = "",
        var phone: String = "",
        var state: Byte = 0,
        var thumbnail: String = ""
) : Parcelable