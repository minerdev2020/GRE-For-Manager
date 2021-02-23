package com.minerdev.greformanager

import android.app.Application
import android.app.ProgressDialog
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.android.volley.Request
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Repository(private val application: Application) {
    private val houseDao: HouseDao
    private val imageDao: ImageDao
    private val lastUpdatedAt: Long
        get() = houseDao.lastUpdatedAt ?: 0

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
        HttpConnection.instance.receive("houses/last-updated-at",
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        if (receivedData.isEmpty()) {
                            deleteAll()

                        } else {
                            checkHouseUpdate(receivedData)
                        }
                    }
                })
    }

    fun loadImages(houseId: Int) {
        HttpConnection.instance.receive("houses/$houseId/images/last-updated-at",
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        if (receivedData.isEmpty()) {
                            deleteAll(houseId)

                        } else {
                            checkImageUpdate(houseId, receivedData)
                        }
                    }
                })
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
                        insert(data)
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

    fun modify(house: House) {
        val progressDialog = ProgressDialog(application)
        progressDialog.setMessage("데이터 전송중...")
        progressDialog.setCancelable(false)

        val houseId = house.id
        HttpConnection.instance.send(Request.Method.PATCH, "houses/$houseId", house,
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val data = Json.decodeFromString<House>(receivedData)
                        update(data)
                        progressDialog.show()
                    }
                })
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

    private fun checkHouseUpdate(receivedData: String) {
        Thread {
            val serverTimestamp = receivedData.toLong()
            val clientTimestamp = lastUpdatedAt

            Log.d("HTTP_DATA", "server last updated time : $serverTimestamp")
            Log.d("HTTP_DATA", "client last updated time : $clientTimestamp")

            if (serverTimestamp > clientTimestamp) {
                loadHousesFromWeb(clientTimestamp)
            }
        }.start()
    }

    private fun loadHousesFromWeb(clientLastUpdatedAt: Long?) {
        HttpConnection.instance.receive("houses", clientLastUpdatedAt,
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val dataList = Json.decodeFromString<List<House>>(receivedData)
                        for (data in dataList) {
                            updateOrInsert(data)
                        }
                    }
                })
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

    private fun getLastUpdatedAt(houseId: Int): Long {
        return imageDao.getLastUpdatedAt(houseId) ?: 0
    }

    private fun checkImageUpdate(houseId: Int, receivedData: String) {
        Thread {
            val serverTimestamp = receivedData.toLong()
            val clientTimestamp = getLastUpdatedAt(houseId)

            if (serverTimestamp > clientTimestamp) {
                deleteAll(houseId)
                loadImagesFromWeb(houseId)
            }
        }.start()
    }

    private fun loadImagesFromWeb(houseId: Int) {
        HttpConnection.instance.receive("houses/$houseId/images",
                object : HttpConnection.OnReceiveListener {
                    override fun onReceive(receivedData: String) {
                        val dataList = Json.decodeFromString<List<House>>(receivedData)
                        for (data in dataList) {
                            updateOrInsert(data)
                        }
                    }
                })
    }
}