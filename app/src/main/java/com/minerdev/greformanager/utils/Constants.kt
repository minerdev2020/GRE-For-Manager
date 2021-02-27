package com.minerdev.greformanager.utils

import android.content.Context
import com.minerdev.greformanager.R
import java.util.*

object Constants {
    const val TAG = "DEBUG_TAG"

    lateinit var BASE_URL: String
    const val API_HOUSE = "/api/houses"
    const val API_IMAGE = "/api/images"
    lateinit var API_DAUM_ADDRESS: String
    const val API_NAVER_MAP = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode"

    lateinit var NAVER_CLIENT_ID: String
    lateinit var NAVER_CLIENT_SECRET: String
    const val NAVER_API_KEY_ID_HEADER = "X-NCP-APIGW-API-KEY-ID"
    const val NAVER_API_KEY_HEADER = "X-NCP-APIGW-API-KEY"

    const val FINISH_INTERVAL_TIME = 2000

    const val FILE_MAX_SIZE: Long = 10485760

    const val SALE = 0
    const val SOLD = 1

    const val PYEONG_TO_METER = 3.305f
    const val METER_TO_PYEONG = 0.3025f

    const val HOUSE_DETAIL_ACTIVITY_REQUEST_CODE = 1
    const val HOUSE_MODIFY_ACTIVITY_REQUEST_CODE = 2

    const val CREATE = 0;
    const val UPDATE = 1;
    const val DELETE = 2;

    val PAYMENT_TYPE = ArrayList<ArrayList<String>>()
    val HOUSE_TYPE = ArrayList<String>()
    val STRUCTURE = ArrayList<String>()
    val DIRECTION = ArrayList<String>()
    val BATHROOM = ArrayList<String>()

    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            HOUSE_TYPE.addAll(context.resources.getStringArray(R.array.houseType))

            val temp = context.resources.getStringArray(R.array.paymentType)
            for (i in 0 until HOUSE_TYPE.size - 1) {
                val list = ArrayList(temp[i].split(" "))
                PAYMENT_TYPE.add(list)
            }

            STRUCTURE.addAll(context.resources.getStringArray(R.array.structure))
            DIRECTION.addAll(context.resources.getStringArray(R.array.direction))
            BATHROOM.addAll(context.resources.getStringArray(R.array.bathroom))

            BASE_URL = context.resources.getString(R.string.web_server_dns)
            API_DAUM_ADDRESS = "$BASE_URL/api/daum-address"

            NAVER_CLIENT_ID = context.getString(R.string.client_id)
            NAVER_CLIENT_SECRET = context.getString(R.string.client_secret)

            isInitialized = true
        }
    }
}