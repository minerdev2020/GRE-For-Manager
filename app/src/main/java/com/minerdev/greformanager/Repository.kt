package com.minerdev.greformanager

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.minerdev.greformanager.db.GreDatabase
import com.minerdev.greformanager.db.HouseDao
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class Repository(private val application: Application) {
    val allSale: LiveData<MutableList<House>>
    val allSold: LiveData<MutableList<House>>
    val images = MutableLiveData<List<Image>>()
    private val houseDao: HouseDao

    init {
        val db: GreDatabase? = GreDatabase.getDatabase(application)
        houseDao = db!!.houseDao()
        allSale = houseDao.allSale
        allSold = houseDao.allSold
    }

    operator fun get(id: Int): House? {
        return houseDao[id]
    }

    fun loadHouses() {
        val sharedPreferences = application.getSharedPreferences("repository", Activity.MODE_PRIVATE)
        var lastUpdatedAt = "0"
        if (sharedPreferences.contains("last_updated_at")) {
            lastUpdatedAt = sharedPreferences.getString("last_updated_at", "0") ?: "0"
        }

        HttpConnection.instance.receive("houses?last_updated_at=$lastUpdatedAt",
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val jsonData = JSONObject(receivedData)
                        val serverLastUpdatedAt = jsonData.getString("updated_at")
                        val editor = sharedPreferences.edit()
                        editor.putString("last_updated_at", serverLastUpdatedAt)
                        editor.apply()

                        if (serverLastUpdatedAt == "0") {
                            deleteAll()

                        } else {
                            val dataList = Json.decodeFromString<List<House>>(jsonData.getString("data"))
                            for (data in dataList) {
                                updateOrInsert(data)
                            }
                        }
                    }
                })
    }

    fun loadImages(houseId: Int) {
        HttpConnection.instance.receive("houses/$houseId/images",
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val jsonData = JSONObject(receivedData)
                        images.postValue(Json.decodeFromString(jsonData.getString("data")))
                    }
                })
    }

    fun add(house: House, images: List<Image>) {
        HttpConnection.instance.send(Request.Method.POST, "houses", house,
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val jsonData = JSONObject(receivedData)
                        val data = Json.decodeFromString<House>(jsonData.getString("data"))
                        val id = data.id
                        for (image in images) {
                            HttpConnection.instance.sendImage(Request.Method.POST,
                                    "houses/$id/images", image, null)
                        }
                    }
                })
    }

    fun modify(id: Int, data: JSONObject) {
        HttpConnection.instance.send(Request.Method.PATCH, "houses/$id", data, null)
    }

    fun modify(house: House, images: List<Image>) {
        val houseId = house.id
        HttpConnection.instance.send(Request.Method.PATCH, "houses/$houseId", house,
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        for (image in images) {
                            when (image.state) {
                                Request.Method.DELETE -> HttpConnection.instance.send(image.state,
                                        "images/${image.id}", null)

                                Request.Method.PATCH -> HttpConnection.instance.send(image.state,
                                        "images/${image.id}", image, null)

                                Request.Method.POST -> HttpConnection.instance.sendImage(image.state,
                                        "houses/$houseId/images", image, null)

                                else -> {
                                }
                            }

                        }
                    }
                })
    }

    fun deleteAll() {
        val sharedPreferences = application.getSharedPreferences("repository", Activity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("last_updated_at", "0")
        editor.apply()

        GreDatabase.databaseWriteExecutor.execute { houseDao.deleteAll() }
    }

    private fun insert(house: House) {
        GreDatabase.databaseWriteExecutor.execute { houseDao.insert(house) }
    }

    private fun update(house: House) {
        GreDatabase.databaseWriteExecutor.execute { houseDao.update(house) }
    }

    private fun updateOrInsert(house: House) {
        GreDatabase.databaseWriteExecutor.execute {
            val result = houseDao[house.id]
            if (result == null) {
                houseDao.insert(house)

            } else {
                houseDao.update(house)
            }
        }
    }
}