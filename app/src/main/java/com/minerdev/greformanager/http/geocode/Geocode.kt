package com.minerdev.greformanager.http.geocode

import android.util.Log
import com.minerdev.greformanager.http.retrofit.RetrofitManager
import com.minerdev.greformanager.utils.Constants.TAG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object Geocode {
    fun getQueryResponseFromNaver(address: String, listener: OnDataReceiveListener?) {
        RetrofitManager.instance.getQueryResponseFromNaver(address,
                { response: String ->
                    run {
                        val format = Json { encodeDefaults = true }
                        val geocodeResult = format.decodeFromString<GeocodeResult>(response)
                        listener?.parseData(geocodeResult)
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, error.localizedMessage ?: "Unknown error!")
                    }
                }
        )
    }

    interface OnDataReceiveListener {
        fun parseData(result: GeocodeResult?)
    }
}