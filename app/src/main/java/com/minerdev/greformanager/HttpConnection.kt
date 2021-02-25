package com.minerdev.greformanager

import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.AppHelper
import com.minerdev.greformanager.utils.Constants
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class HttpConnection private constructor() {
    fun receive(uri: String, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        Log.d("HTTP_DATA", "receive : $serverUri")
        makeJsonRequest(Request.Method.GET, serverUri, null, listener)
    }

    fun send(method: Int, uri: String, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        Log.d("HTTP_DATA", "send : $serverUri")
        makeJsonRequest(method, serverUri, null, listener)
    }

    fun send(method: Int, uri: String, data: JSONObject, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val json = data.toString()
        Log.d("HTTP_DATA", "send : $json")
        makeJsonRequest(method, serverUri, json, listener)
    }

    fun send(method: Int, uri: String, data: House, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val format = Json { encodeDefaults = true }
        val json = format.encodeToString(data)
        val prettyJson = Json { prettyPrint = true; encodeDefaults = true; }
        Log.d("HTTP_DATA", "send : \n" + prettyJson.encodeToString(data))
        makeJsonRequest(method, serverUri, json, listener)
    }

    fun send(method: Int, uri: String, data: Image, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val format = Json { encodeDefaults = true }
        val json = format.encodeToString(data)
        val prettyJson = Json { prettyPrint = true; encodeDefaults = true; }
        Log.d("HTTP_DATA", "send : \n" + prettyJson.encodeToString(data))
        makeJsonRequest(method, serverUri, json, listener)
    }

    fun sendImage(method: Int, uri: String, data: Image, listener: OnReceiveListener?) {
        val serverUri: String = Constants.instance.DNS + "/api/" + uri
        val prettyJson = Json { prettyPrint = true }
        Log.d("HTTP_DATA", "send : \n" + prettyJson.encodeToString(data))
        makeImageRequest(method, serverUri, data, listener)
    }

    private fun makeJsonRequest(method: Int, uri: String, json: String?, listener: OnReceiveListener?) {
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

    private fun makeImageRequest(method: Int, uri: String, image: Image, listener: OnReceiveListener?) {
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
                params["position"] = image.position.toString()
                params["thumbnail"] = image.thumbnail.toString()
                return params
            }

            override val byteData: Map<String, DataPart>
                get() {
                    val params: MutableMap<String, DataPart> = HashMap()
                    image.localUri?.let {
                        val imageUri = Uri.parse(image.localUri)
                        params["image"] = DataPart("image", AppHelper.instance.getByteArrayFromUri(imageUri))
                    }
                    return params
                }
        }

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false)
        AppHelper.instance.requestQueue?.add(request)
    }

    private fun makeImageRequest2(method: Int, uri: String, image: Image, listener: OnReceiveListener?) {
        val request = Request()
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
                params["position"] = image.position.toString()
                params["thumbnail"] = image.thumbnail.toString()
                return params
            }

            override val byteData: Map<String, DataPart>
                get() {
                    val params: MutableMap<String, DataPart> = HashMap()
                    image.localUri?.let {
                        val imageUri = Uri.parse(image.localUri)
                        params["image"] = DataPart("image", AppHelper.instance.getByteArrayFromUri(imageUri))
                    }
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