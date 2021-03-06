package com.minerdev.greformanager

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minerdev.greformanager.http.retrofit.RetrofitManager
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.Constants.CREATE
import com.minerdev.greformanager.utils.Constants.DELETE
import com.minerdev.greformanager.utils.Constants.TAG
import com.minerdev.greformanager.utils.Constants.UPDATE
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class Repository {
    val allSale = MutableLiveData<List<House>>()
    val allSold = MutableLiveData<List<House>>()
    val house = MutableLiveData<House>()

    val allImages = MutableLiveData<List<Image>>()

    private var size = 0
    private var count = 0

    fun loadHouse(id: Int) {
        RetrofitManager.instance.getHouse(id,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "loadHouse response : " + data.getString("message"))
                        Log.d(TAG, "loadHouse response : " + data.getString("data"))

                        val format = Json { encodeDefaults = true }
                        house.postValue(format.decodeFromString<House>(data.getString("data")))
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "loadHouse sale error : " + error.localizedMessage)
                    }
                })
    }

    fun loadHouses() {
        RetrofitManager.instance.getAllHouse(0,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "loadHouses sale response : " + data.getString("message"))
                        Log.d(TAG, "loadHouses sale response : " + data.getString("data"))

                        val format = Json { encodeDefaults = true }
                        val houses = format.decodeFromString<List<House>>(data.getString("data"))
                        allSale.postValue(houses)
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "loadHouses sale error : " + error.localizedMessage)
                    }
                })

        RetrofitManager.instance.getAllHouse(1,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "loadHouses sold response : " + data.getString("message"))
                        Log.d(TAG, "loadHouses sold response : " + data.getString("data"))

                        val format = Json { encodeDefaults = true }
                        val houses = format.decodeFromString<List<House>>(data.getString("data"))
                        allSold.postValue(houses)
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "loadHouses sold error : " + error.localizedMessage)
                    }
                })
    }

    fun loadImages(houseId: Int) {
        RetrofitManager.instance.getAllImage(houseId,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "loadImages response : " + data.getString("message"))
                        Log.d(TAG, "loadImages response : " + data.getString("data"))

                        val format = Json { encodeDefaults = true }
                        val images = format.decodeFromString<List<Image>>(data.getString("data"))
                        allImages.postValue(images)
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "loadImages error : " + error.localizedMessage)
                    }
                })
    }

    fun add(house: House, images: List<Image>, onResponse: () -> Unit) {
        size = images.size
        count = 0
        RetrofitManager.instance.createHouse(house,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "add response : " + data.getString("message"))
                        Log.d(TAG, "add response : " + data.getString("data"))

                        val houseData = Json.decodeFromString<House>(data.getString("data"))

                        for (image in images) {
                            createImage(houseData.id, image, onResponse)
                        }
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "add error : " + error.localizedMessage)
                    }
                })
    }

    fun modify(house: House, images: List<Image>, onResponse: () -> Unit) {
        size = images.size
        count = 0
        RetrofitManager.instance.updateHouse(house.id, house,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "modify response : " + data.getString("message"))

                        for (image in images) {
                            when (image.state) {
                                CREATE -> createImage(house.id, image, onResponse)
                                UPDATE -> updateImage(image.id, image, onResponse)
                                DELETE -> deleteImage(image.id, onResponse)
                                else -> {
                                }
                            }
                        }
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "modify error : " + error.localizedMessage)
                    }
                })
    }

    fun modifyHouseState(id: Int, state: Byte) {
        RetrofitManager.instance.updateHouse(id, state,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "modifyHouseState response : " + data.getString("message"))
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "modifyHouseState error : " + error.localizedMessage)
                    }
                })
    }

    private fun createImage(houseId: Int, image: Image, onResponse: () -> Unit) {
        RetrofitManager.instance.createImage(houseId, image,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "createImage response : " + data.getString("message"))
                        Log.d(TAG, "createImage response : " + data.getString("data"))
                        count++
                        if (count == size) {
                            onResponse()
                        }
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "createImage error : " + error.localizedMessage)
                        count++
                        if (count == size) {
                            onResponse()
                        }
                    }
                })
    }

    private fun updateImage(id: Int, image: Image, onResponse: () -> Unit) {
        RetrofitManager.instance.updateImage(id, image,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "updateImage response : " + data.getString("message"))
                        count++
                        if (count == size) {
                            onResponse()
                        }
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "updateImage error : " + error.localizedMessage)
                        count++
                        if (count == size) {
                            onResponse()
                        }
                    }
                })
    }

    private fun deleteImage(id: Int, onResponse: () -> Unit) {
        RetrofitManager.instance.deleteImage(id,
                { response: String ->
                    run {
                        val data = JSONObject(response)
                        Log.d(TAG, "deleteImage response : " + data.getString("message"))
                        count++

                        if (count == size) {
                            onResponse()
                        }
                    }
                },
                { error: Throwable ->
                    run {
                        Log.d(TAG, "deleteImage error : " + error.localizedMessage)
                        count++
                        if (count == size) {
                            onResponse()
                        }
                    }
                })
    }
}