package com.minerdev.greformanager

import android.app.Application
import androidx.lifecycle.LiveData

class ImageRepository(application: Application) {
    private val imageDao: ImageDao?
    val allImages: LiveData<List<Image>>?
    fun getLastUpdatedAt(house_id: Int): Long? {
        return imageDao!!.getLastUpdatedAt(house_id)
    }

    fun getOrderByPosition(house_id: Int): LiveData<List<Image>> {
        return imageDao!!.getImages(house_id)
    }

    fun insert(image: Image) {
        GreDatabase.databaseWriteExecutor.execute { imageDao!!.insert(image) }
    }

    fun insert(images: List<Image>) {
        GreDatabase.databaseWriteExecutor.execute { imageDao!!.insert(images) }
    }

    fun updateOrInsert(image: Image) {
        GreDatabase.run {
            databaseWriteExecutor.execute {
                val result = imageDao!!.getImage(image.house_id, image.position.toInt())
                if (result == null) {
                    imageDao.insert(image)
                } else {
                    imageDao.update(image)
                }
            }
        }
    }

    fun deleteAll(house_id: Int) {
        GreDatabase.databaseWriteExecutor.execute { imageDao!!.deleteAll(house_id) }
    }

    fun deleteAll() {
        GreDatabase.databaseWriteExecutor.execute { imageDao!!.deleteAll() }
    }

    init {
        val db: GreDatabase? = GreDatabase.getDatabase(application)
        imageDao = db!!.imageDao()
        allImages = imageDao.allImages
    }
}