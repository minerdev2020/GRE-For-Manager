package com.minerdev.greformanager

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(
        @PrimaryKey var id: Int = 0,
        var created_at: Long = 0,
        var updated_at: Long = 0,
        var title: String? = null,
        var path: String? = null,
        var position: Byte = 0,
        var thumbnail: Byte = 0,
        var house_id: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte(),
            parcel.readByte(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(created_at)
        parcel.writeLong(updated_at)
        parcel.writeString(title)
        parcel.writeString(path)
        parcel.writeByte(position)
        parcel.writeByte(thumbnail)
        parcel.writeInt(house_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}