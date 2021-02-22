package com.minerdev.greformanager

import android.content.Context
import android.net.Uri
import com.android.volley.RequestQueue
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class AppHelper private constructor() {
    var requestQueue: RequestQueue? = null

    fun getPathFromUri(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToNext()
        val path = cursor.getString(cursor.getColumnIndex("_data"))
        cursor.close()
        return path
    }

    fun getByteArrayFromUri(context: Context, uri: Uri): ByteArray {
        val file = File(getPathFromUri(context, uri))
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var size: Int
        if (file.exists() && file.canRead()) {
            try {
                val inputStream = FileInputStream(file)

                while (inputStream.read(buf).also { size = it } != -1) {
                    byteArrayOutputStream.write(buf, 0, size)
                }

                inputStream.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return byteArrayOutputStream.toByteArray()
    }

    fun getDiffTimeMsg(since: Long): String {
        var diffTime = System.currentTimeMillis() / 1000 - since
        val msg: String
        when {
            diffTime < 60 -> {
                msg = "방금전"
            }

            60.let { diffTime /= it; diffTime } < 60 -> {
                msg = diffTime.toString() + "분전"
            }

            60.let { diffTime /= it; diffTime } < 24 -> {
                msg = diffTime.toString() + "시간전"
            }

            24.let { diffTime /= it; diffTime } < 30 -> {
                msg = diffTime.toString() + "일전"
            }

            30.let { diffTime /= it; diffTime } < 12 -> {
                msg = diffTime.toString() + "개월전"
            }

            else -> {
                msg = diffTime.toString() + "년전"
            }
        }

        return msg
    }

    private object Holder {
        val INSTANCE = AppHelper()
    }

    companion object {
        val instance: AppHelper
            get() = Holder.INSTANCE
    }
}