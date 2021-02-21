package com.minerdev.greformanager

import android.content.Context
import java.util.*

class Constants private constructor() {
    val SALE = 0
    val SOLD = 1
    val PYEONG_TO_METER = 3.305f
    val METER_TO_PYEONG = 0.3025f
    val HOUSE_DETAIL_ACTIVITY_REQUEST_CODE = 1
    val HOUSE_MODIFY_ACTIVITY_REQUEST_CODE = 2
    val FILE_MAX_SIZE: Long = 10485760
    val PAYMENT_TYPE = ArrayList<ArrayList<String>>()
    val HOUSE_TYPE = ArrayList<String>()
    val STRUCTURE = ArrayList<String>()
    val DIRECTION = ArrayList<String>()
    val BATHROOM = ArrayList<String>()
    var DNS: String? = null

    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            Collections.addAll(HOUSE_TYPE, *context.resources.getStringArray(R.array.houseType))

            val temp = context.resources.getStringArray(R.array.paymentType)

            for (i in 0 until HOUSE_TYPE.size - 1) {
                val list = ArrayList(listOf(*temp[i].split(" ").toTypedArray()))
                PAYMENT_TYPE.add(list)
            }

            Collections.addAll(STRUCTURE, *context.resources.getStringArray(R.array.structure))
            Collections.addAll(DIRECTION, *context.resources.getStringArray(R.array.direction))
            Collections.addAll(BATHROOM, *context.resources.getStringArray(R.array.bathroom))

            DNS = context.resources.getString(R.string.web_server_dns)

            isInitialized = true
        }
    }

    private object Holder {
        val INSTANCE = Constants()
    }

    companion object {
        val instance: Constants
            get() = Holder.INSTANCE
    }
}