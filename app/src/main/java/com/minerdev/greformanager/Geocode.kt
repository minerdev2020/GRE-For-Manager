package com.minerdev.greformanager

import android.content.Context
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import java.util.*

class Geocode private constructor() {
    private var result: GeocodeResult? = null
    private var listener: OnDataReceiveListener? = null

    fun setOnDataReceiveListener(listener: OnDataReceiveListener?) {
        this.listener = listener
    }

    fun getQueryResponseFromNaver(context: Context, address: String) {
        val apiURL = context.getString(R.string.naver_open_api_geocode) + "?query=" + address
        val request: StringRequest = object : StringRequest(Method.GET, apiURL,
                Response.Listener { response: String? ->
                    try {
                        val gson = Gson()
                        result = gson.fromJson(response, GeocodeResult::class.java)
                        if (result != null) {
                            listener!!.parseData(result)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError -> Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show() }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return HashMap()
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val client_id = context.getString(R.string.client_id)
                val client_secret = context.getString(R.string.client_secret)
                val params: MutableMap<String, String> = HashMap()
                params[context.getString(R.string.api_key_id_header)] = client_id
                params[context.getString(R.string.api_key_header)] = client_secret
                return params
            }
        }

        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false)
        AppHelper.instance.requestQueue?.add(request)
    }

    interface OnDataReceiveListener {
        fun parseData(result: GeocodeResult?)
    }

    private object Holder {
        val INSTANCE = Geocode()
    }

    companion object {
        val instance: Geocode
            get() = Holder.INSTANCE
    }
}