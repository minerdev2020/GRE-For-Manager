package com.minerdev.greformanager

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.nio.charset.StandardCharsets
import java.util.*

class HttpConnection private constructor() {
    fun receive(context: Context, uri: String, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        Log.d("SEND_DATA", serverUri)
        makeRequest(context, Request.Method.GET, serverUri, null, listener)
    }

    fun send(context: Context, method: Int, uri: String, data: JsonObject, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val json = data.toString()
        Log.d("SEND_DATA", json)
        makeRequest(context, method, serverUri, json, listener)
    }

    fun send(context: Context, method: Int, uri: String, data: House, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val gson = Gson()
        val json = gson.toJson(data)
        Log.d("SEND_DATA", json)
        makeRequest(context, method, serverUri, json, listener)
    }

    fun send(context: Context, method: Int, uri: String, imageUris: List<Uri>, images: List<Image>, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        for (i in imageUris.indices) {
            imageUris[i].path?.let { Log.d("SEND_DATA", it) }
            makeImageRequest(context, method, serverUri, imageUris[i], images[i], listener)
        }
    }

    private fun makeRequest(context: Context, method: Int, uri: String, json: String?, listener: OnReceiveListener?) {
        val request: StringRequest = object : StringRequest(method, uri,
                Response.Listener { response: String? ->
                    Toast.makeText(context, "데이터 전송 성공.", Toast.LENGTH_SHORT).show()
                    listener?.onReceive(response)
                },
                Response.ErrorListener { error: VolleyError ->
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    Log.e("HTTP_ERROR", error.message ?: "null")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return if (json == null || json == "") super.getBody() else json.toByteArray(StandardCharsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return if (json == null || json == "") super.getBodyContentType() else "application/json"
            }
        }

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false)
        AppHelper.instance.requestQueue?.add(request)
    }

    private fun makeImageRequest(context: Context, method: Int, uri: String, imageUri: Uri?, image: Image?, listener: OnReceiveListener?) {
        val request: VolleyMultipartRequest = object : VolleyMultipartRequest(method, uri,
                Response.Listener { response: NetworkResponse ->
                    Toast.makeText(context, "데이터 전송 성공.", Toast.LENGTH_SHORT).show()
                    listener?.onReceive(String(response.data))
                },
                Response.ErrorListener { error: VolleyError ->
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                    Log.e("HTTP_ERROR", error.message ?: "null")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["title"] = image!!.title!!
                params["position"] = image.position.toString()
                if (image.thumbnail.toInt() == 1) {
                    params["thumbnail"] = "1"
                } else {
                    params["thumbnail"] = "0"
                }
                return params
            }

            override val byteData: Map<String, DataPart>?
                get() {
                    val params: MutableMap<String, DataPart> = HashMap()
                    params["image"] = DataPart(image!!.title, AppHelper.instance.getByteArrayFromUri(context, imageUri))
                    return params
                }
        }

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false)
        AppHelper.instance.requestQueue?.add(request)
    }

    interface OnReceiveListener {
        fun onReceive(receivedData: String?)
    }

    private object Holder {
        val INSTANCE = HttpConnection()
    }

    companion object {
        val instance: HttpConnection
            get() = Holder.INSTANCE
    }
}