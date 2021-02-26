package com.minerdev.greformanager.http.retrofit

import android.util.Log
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.Constants
import com.minerdev.greformanager.utils.Constants.BASE_URL
import com.minerdev.greformanager.utils.Constants.NAVER_CLIENT_ID
import com.minerdev.greformanager.utils.Constants.NAVER_CLIENT_SECRET
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RetrofitManager {
    companion object {
        val instance = RetrofitManager()
    }

    private val iRetrofit: IRetrofit? = RetrofitClient.getClient(BASE_URL)?.create(IRetrofit::class.java)

    // 네이버 지도 서비스
    fun getQueryResponseFromNaver(address: String, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.getQueryResponseFromNaver(
                NAVER_CLIENT_ID,
                NAVER_CLIENT_SECRET,
                address)
                ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    // 매물 정보
    fun getAllHouse(state: Byte, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.getAllHouse(state) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getHouse(id: Int, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.getHouse(id) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createHouse(house: House, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.createHouse(house) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun updateHouse(id: Int, state: Byte, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        Log.d(Constants.TAG, "state : $state")
        val call = iRetrofit?.updateHouse(id, state) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun updateHouse(id: Int, house: House, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        Log.d(Constants.TAG, house.toString())
        val call = iRetrofit?.updateHouse(id, house) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteHouse(id: Int, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.deleteHouse(id) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteAllHouse(onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.deleteAllHouse() ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }


    // 매물 이미지 정보
    fun getAllImage(houseId: Int, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.getAllImage(houseId) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getImage(houseId: Int, position: Int, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.getImage(houseId, position) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createImage(houseId: Int, image: Image, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val file = File(image.localUri!!)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val call = iRetrofit?.createImage(houseId, image.position, image.thumbnail, body) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun updateImage(id: Int, image: Image, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.updateImage(id, image.position, image.thumbnail) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteImage(id: Int, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.deleteImage(id) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteAllImage(houseId: Int, onResponse: (response: String) -> Unit, onFailure: (error: Throwable) -> Unit) {
        val call = iRetrofit?.deleteAllImage(houseId) ?: return
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    onResponse(response.body().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onFailure(t)
            }
        })
    }

}