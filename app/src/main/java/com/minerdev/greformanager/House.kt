package com.minerdev.greformanager

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class House(
        @PrimaryKey var id: Int = 0,
        var created_at: Long = 0,
        var updated_at: Long = 0,
        var address: String? = null,
        var number: String? = null,
        var house_type: Byte = 0,
        var facility: Byte = 0,
        var payment_type: Byte = 0,
        var price: Int = 0,
        var deposit: Int = 0,
        var monthly_rent: Int = 0,
        var premium: Int = 0,
        var manage_fee: Int = 0,
        var manage_fee_contains: String? = null,
        var area_meter: Float = 0f,
        var rent_area_meter: Float = 0f,
        var building_floor: Byte = 0,
        var floor: Byte = 0,
        var structure: Byte = 0,
        var bathroom: Byte = 0,
        var bathroom_location: Byte = 0,
        var direction: Byte = 0,
        var built_date: String? = null,
        var move_date: String? = null,
        var options: String? = null,
        var detail_info: String? = null,
        var phone: String? = null,
        var state: Byte = 0,
        var thumbnail: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(created_at)
        parcel.writeLong(updated_at)
        parcel.writeString(address)
        parcel.writeString(number)
        parcel.writeByte(house_type)
        parcel.writeByte(facility)
        parcel.writeByte(payment_type)
        parcel.writeInt(price)
        parcel.writeInt(deposit)
        parcel.writeInt(monthly_rent)
        parcel.writeInt(premium)
        parcel.writeInt(manage_fee)
        parcel.writeString(manage_fee_contains)
        parcel.writeFloat(area_meter)
        parcel.writeFloat(rent_area_meter)
        parcel.writeByte(building_floor)
        parcel.writeByte(floor)
        parcel.writeByte(structure)
        parcel.writeByte(bathroom)
        parcel.writeByte(bathroom_location)
        parcel.writeByte(direction)
        parcel.writeString(built_date)
        parcel.writeString(move_date)
        parcel.writeString(options)
        parcel.writeString(detail_info)
        parcel.writeString(phone)
        parcel.writeByte(state)
        parcel.writeString(thumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<House> {
        override fun createFromParcel(parcel: Parcel): House {
            return House(parcel)
        }

        override fun newArray(size: Int): Array<House?> {
            return arrayOfNulls(size)
        }
    }
}