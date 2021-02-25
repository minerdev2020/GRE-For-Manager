package com.minerdev.greformanager.retrofit

import com.google.gson.JsonElement
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.Constants.BASE_URL
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {
    companion object {
        val instance = RetrofitManager()
    }

    private val iRetrofit: IRetrofit? = RetrofitClient.getClient(BASE_URL)?.create(IRetrofit::class.java)

    // 네이버 지도 서비스
    fun getQueryResponseFromNaver(address: String, onResponse: (String) -> Unit, onFailure: (String) -> Unit) {
        val call = iRetrofit?.getQueryResponseFromNaver(address) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
            }
        })
    }

    // 매물 정보
    fun getAllHouse(lastUpdatedAt: String, onResponse: (String) -> Unit, onFailure: (String) -> Unit) {
        val call = iRetrofit?.getAllHouse(lastUpdatedAt) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
            }
        })
    }

    fun getHouse(id: Int, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.getHouse(id) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createHouse(house: House, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.createHouse(house) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun updateHouse(id: Int, house: House, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.updateHouse(id, house) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteHouse(id: Int, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.deleteHouse(id) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteAllHouse(id: Int, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.deleteAllHouse(id) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }


    // 매물 이미지 정보
    fun getAllImage(houseId: Int, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.getAllImage(houseId) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getImage(houseId: Int, position: Int, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.getImage(houseId, position) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createImage(houseId: Int, image: Image, imageFile: RequestBody, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.createImage(houseId, image, imageFile) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun updateImage(id: Int, image: Image, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.updateImage(id, image) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteImage(id: Int, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.deleteImage(id) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteAllImage(houseId: Int, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = iRetrofit?.deleteAllImage(houseId) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                onResponse(response.raw().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                onFailure(t)
            }
        })
    }

}