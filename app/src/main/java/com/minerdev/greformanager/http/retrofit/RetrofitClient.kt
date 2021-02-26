package com.minerdev.greformanager.http.retrofit

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.minerdev.greformanager.utils.Constants.TAG
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private var retrofitClient: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        Log.d(TAG, "RetrofitClient - getClient() called")

        if (retrofitClient == null) {
            val contentType = "application/json".toMediaType()
            retrofitClient = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(Json.asConverterFactory(contentType))
                    .build()
        }

        return retrofitClient
    }
}