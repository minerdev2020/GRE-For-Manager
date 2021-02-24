package com.minerdev.greformanager

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.net.Uri
import androidx.lifecycle.LiveData
import com.android.volley.Request
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class Repository(private val application: Application) {
    private val houseDao: HouseDao
    private val imageDao: ImageDao

    val allSale: LiveData<MutableList<House>>
    val allSold: LiveData<MutableList<House>>

    init {
        val db: GreDatabase? = GreDatabase.getDatabase(application)
        houseDao = db!!.houseDao()
        imageDao = db.imageDao()
        allSale = houseDao.allSale
        allSold = houseDao.allSold
    }

    operator fun get(id: Int): House? {
        return houseDao[id]
    }

    fun getOrderByPosition(houseId: Int): LiveData<List<Image>> {
        return imageDao.getImages(houseId)
    }

    fun loadHouses() {
        val sharedPreferences = application.getSharedPreferences("repository", Activity.MODE_PRIVATE)
        var lastUpdatedAt = "0"
        if (sharedPreferences.contains("last_updated_at")) {
            lastUpdatedAt = sharedPreferences.getString("last_updated_at", "0") ?: "0"
        }

        Thread {
            HttpConnection.instance.receive("houses?last_updated_at=$lastUpdatedAt",
                    object : HttpConnection.OnReceiveListener {
                        override fun onReceive(receivedData: String) {
                            val jsonData = JSONObject(receivedData)
                            val editor = sharedPreferences.edit()
                            editor.putString("last_updated_at", jsonData.getString("updated_at"))
                            editor.apply()

                            val dataList = Json.decodeFromString<List<House>>(jsonData.getString("data"))
                            for (data in dataList) {
                                updateOrInsert(data)
                            }
                        }
                    })
        }.start()
    }

    fun loadImages(houseId: Int) {
        val sharedPreferences = application.getSharedPreferences("repository", Activity.MODE_PRIVATE)
        var lastUpdatedAt = "0"
        if (sharedPreferences.contains("last_updated_at")) {
            lastUpdatedAt = sharedPreferences.getString("last_updated_at", "0") ?: "0"
        }

        Thread {
            HttpConnection.instance.receive("houses/$houseId/images?last_updated_at=$lastUpdatedAt",
                    object : HttpConnection.OnReceiveListener {
                        override fun onReceive(receivedData: String) {
                            val jsonData = JSONObject(receivedData)
                            val editor = sharedPreferences.edit()
                            editor.putString("last_updated_at", jsonData.getString("updated_at"))
                            editor.apply()

                            val dataList = Json.decodeFromString<List<Image>>(jsonData.getString("data"))
                            for (data in dataList) {
                                updateOrInsert(data)
                            }
                        }
                    })
        }.start()
    }

    fun add(house: House, imageUris: List<Uri>, images: List<Image>) {
        val progressDialog = ProgressDialog(application)
        progressDialog.setMessage("데이터 전송중...")
        progressDialog.setCancelable(false)

        var count = 0
        HttpConnection.instance.send(Request.Method.POST, "houses", house,
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val data = Json.decodeFromString<House>(receivedData)
                        val id = data.id
                        HttpConnection.instance.send(Request.Method.POST, "houses/$id/images", imageUris, images,
                                object : HttpConnection.OnReceiveListener {
                                    override fun onReceive(receivedData: String) {
                                        count++
                                        val imageData = Json.decodeFromString<Image>(receivedData)
                                        insert(imageData)

                                        if (count == imageUris.size) {
                                            progressDialog.dismiss()
                                        }
                                    }
                                })

                        progressDialog.show()
                    }
                })
    }

    fun modify(id: Int, data: JSONObject) {
        HttpConnection.instance.send(Request.Method.PATCH, "houses/$id", data, null)
    }

    fun modify(house: House, imageUris: List<Uri>, images: List<Image>) {
        val progressDialog = ProgressDialog(application)
        progressDialog.setMessage("데이터 전송중...")
        progressDialog.setCancelable(false)

        val houseId = house.id
        var count = 0
        HttpConnection.instance.send(Request.Method.PATCH, "houses/$houseId", house,
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val data = Json.decodeFromString<House>(receivedData)
                        update(data)

                        HttpConnection.instance.send(Request.Method.POST, "houses/$houseId/images", imageUris, images,
                                object : HttpConnection.OnReceiveListener {
                                    override fun onReceive(receivedData: String) {
                                        count++
                                        val imageData = Json.decodeFromString<Image>(receivedData)
                                        updateOrInsert(imageData)

                                        if (count == imageUris.size) {
                                            progressDialog.dismiss()
                                        }
                                    }
                                })

                        progressDialog.show()
                    }
                })
    }

    fun deleteAll() {
        GreDatabase.databaseWriteExecutor.execute { houseDao.deleteAll() }
    }

    fun deleteAll(house_id: Int) {
        GreDatabase.databaseWriteExecutor.execute { imageDao.deleteAll(house_id) }
    }

    fun deleteAllImages() {
        GreDatabase.databaseWriteExecutor.execute { imageDao.deleteAll() }
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

    private fun insert(image: Image) {
        GreDatabase.databaseWriteExecutor.execute { imageDao.insert(image) }
    }

    private fun updateOrInsert(image: Image) {
        GreDatabase.run {
            databaseWriteExecutor.execute {
                val result = imageDao.getImage(image.house_id, image.position.toInt())
                if (result == null) {
                    imageDao.insert(image)
                } else {
                    imageDao.update(image)
                }
            }
        }
    }
}