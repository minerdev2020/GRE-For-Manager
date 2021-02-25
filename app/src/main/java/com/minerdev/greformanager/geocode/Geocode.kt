package com.minerdev.greformanager.geocode

import com.minerdev.greformanager.retrofit.RetrofitManager

class Geocode private constructor() {
    private lateinit var result: GeocodeResult
    private var listener: OnDataReceiveListener? = null

    fun setOnDataReceiveListener(listener: OnDataReceiveListener?) {
        this.listener = listener
    }

    fun getQueryResponseFromNaver(address: String) {
        RetrofitManager.instance.getQueryResponseFromNaver(address, null, null)
    }

    interface OnDataReceiveListener {
        fun parseData(result: GeocodeResult?)
    }

    private object Holder {
        val INSTANCE = Geocode()
    }

    companion object {
        val instance: Geocode
            get() = Holder.INSTANCE
    }
}