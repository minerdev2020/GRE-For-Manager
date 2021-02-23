package com.minerdev.greformanager

import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

class HttpConnection private constructor() {
    fun receive(uri: String, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        Log.d("HTTP_DATA", "receive : $serverUri")
        makeHouseRequest(Request.Method.GET, serverUri, null, listener)
    }

    fun receive(uri: String, clientLastUpdatedAt: Long?, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        Log.d("HTTP_DATA", "receive : $serverUri, $clientLastUpdatedAt")
        makeHouseRequest(Request.Method.GET, serverUri, clientLastUpdatedAt.toString(), listener)
    }

    fun send(method: Int, uri: String, data: JsonObject, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val json = data.toString()
        Log.d("HTTP_DATA", "send : $json")
        makeHouseRequest(method, serverUri, json, listener)
    }

    fun send(method: Int, uri: String, data: House, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val json = Json.encodeToString(data)
        val prettyJson = Json { prettyPrint = true }
        Log.d("HTTP_DATA", "send : \n" + prettyJson.encodeToString(data))
        makeHouseRequest(method, serverUri, json, listener)
    }

    fun send(method: Int, uri: String, imageUris: List<Uri>, images: List<Image>, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        for (i in imageUris.indices) {
            imageUris[i].path?.let { Log.d("HTTP_DATA", "send : $it") }
            makeImageRequest(method, serverUri, imageUris[i], images[i], listener)
        }
    }

    private fun makeHouseRequest(method: Int, uri: String, json: String?, listener: OnReceiveListener?) {
        val request: StringRequest = object : StringRequest(method, uri,
                Response.Listener { response ->
                    Log.d("HTTP_DATA", "makeHouseRequest response : $response")

                    if (!response.isNullOrEmpty()) {
                        listener?.onReceive(response)
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("HTTP_DATA", "makeHouseRequest error : " + error.message)
                }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["client_last_updated_at"] = json ?: ""
                return params
            }

            override fun getBody(): ByteArray {
                return if (json.isNullOrEmpty()) super.getBody() else json.toByteArray(StandardCharsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return if (json.isNullOrEmpty()) super.getBodyContentType() else "application/json"
            }
        }

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false)
        AppHelper.instance.requestQueue?.add(request)
    }

    private fun makeImageRequest(method: Int, uri: String, imageUri: Uri, image: Image, listener: OnReceiveListener?) {
        val request: VolleyMultipartRequest = object : VolleyMultipartRequest(method, uri,
                Response.Listener { response ->
                    Log.d("HTTP_DATA", "makeImageRequest response")

                    response?.let {
                        if (it.data != null && it.data.isNotEmpty()) {
                            listener?.onReceive(String(it.data))
                        }
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("HTTP_DATA", "makeImageRequest error : " + error.message)
                }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["title"] = image.title
                params["position"] = image.position.toString()

                if (image.thumbnail.toInt() == 1) {
                    params["thumbnail"] = "1"

                } else {
                    params["thumbnail"] = "0"
                }

                return params
            }

            override val byteData: Map<String, DataPart>
                get() {
                    val params: MutableMap<String, DataPart> = HashMap()
                    params["image"] = DataPart(image.title, AppHelper.instance.getByteArrayFromUri(imageUri))
                    return params
                }
        }

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false)
        AppHelper.instance.requestQueue?.add(request)
    }

    interface OnReceiveListener {
        fun onReceive(receivedData: String)
    }

    private object Holder {
        val INSTANCE = HttpConnection()
    }

    companion object {
        val instance: HttpConnection
            get() = Holder.INSTANCE
    }
}