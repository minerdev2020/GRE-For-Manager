package com.minerdev.greformanager.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.*

object AppHelper {
    fun getPathFromUri(context: Context, uri: String): String {
        val cursor = context.contentResolver.query(Uri.parse(uri), null, null, null, null)
        cursor?.let {
            it.moveToNext()
            val path = it.getString(it.getColumnIndex("_data"))
            it.close()
            return path
        }

        return ""
    }

    fun getPathFromUri(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            it.moveToNext()
            val path = it.getString(it.getColumnIndex("_data"))
            it.close()
            return path
        }

        return ""
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

    fun getDiffTimeMsg(since: String): String {
        val sinceTime = getTime(since)
        var diffTime = System.currentTimeMillis() / 1000 - sinceTime

        val msg: String
        when {
            diffTime < 60 -> msg = "방금전"
            60.let { diffTime /= it; diffTime } < 60 -> msg = diffTime.toString() + "분전"
            60.let { diffTime /= it; diffTime } < 24 -> msg = diffTime.toString() + "시간전"
            24.let { diffTime /= it; diffTime } < 30 -> msg = diffTime.toString() + "일전"
            30.let { diffTime /= it; diffTime } < 12 -> msg = diffTime.toString() + "개월전"
            else -> msg = diffTime.toString() + "년전"
        }

        return msg
    }

    private fun getTime(time: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = format.parse(time.replace('T', ' ').substring(0, time.length - 5))
        return date.time
    }
}