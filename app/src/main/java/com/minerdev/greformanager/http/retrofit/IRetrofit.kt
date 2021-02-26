package com.minerdev.greformanager.http.retrofit

import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.utils.Constants.API_HOUSE
import com.minerdev.greformanager.utils.Constants.API_IMAGE
import com.minerdev.greformanager.utils.Constants.API_NAVER_MAP
import com.minerdev.greformanager.utils.Constants.NAVER_API_KEY_HEADER
import com.minerdev.greformanager.utils.Constants.NAVER_API_KEY_ID_HEADER
import kotlinx.serialization.json.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {
    // 네이버 지도 서비스
    @GET(API_NAVER_MAP)
    fun getQueryResponseFromNaver(
            @Header(NAVER_API_KEY_ID_HEADER) clientId: String,
            @Header(NAVER_API_KEY_HEADER) clientSecret: String,
            @Query("query") address: String
    ): Call<JsonObject>

    // 매물 정보
    @GET(API_HOUSE)
    fun getAllHouse(@Query("state") state: Byte = 0): Call<JsonObject>

    @GET("$API_HOUSE/{id}")
    fun getHouse(@Path("id") id: Int): Call<JsonObject>

    @POST(API_HOUSE)
    fun createHouse(@Body house: House): Call<JsonObject>

    @PUT("$API_HOUSE/{id}")
    fun updateHouse(
            @Path("id") id: Int,
            @Body house: House
    ): Call<JsonObject>

    @PATCH("$API_HOUSE/{id}")
    fun updateHouse(
            @Path("id") id: Int,
            @Query("state") state: Byte
    ): Call<JsonObject>

    @DELETE("$API_HOUSE/{id}")
    fun deleteHouse(@Path("id") id: Int): Call<JsonObject>

    @DELETE(API_HOUSE)
    fun deleteAllHouse(): Call<JsonObject>


    // 매물 이미지 정보
    @GET("$API_HOUSE/{house_id}/images")
    fun getAllImage(@Path("house_id") houseId: Int): Call<JsonObject>

    @GET("$API_HOUSE/{house_id}/images/{position}")
    fun getImage(
            @Path("house_id") houseId: Int,
            @Path("position") position: Int
    ): Call<JsonObject>

    @Multipart
    @POST("$API_HOUSE/{house_id}/images")
    fun createImage(
            @Path("house_id") houseId: Int,
            @Query("position") position: Byte,
            @Query("thumbnail") thumbnail: Byte,
            @Part imageFile: MultipartBody.Part
    ): Call<JsonObject>

    @PATCH("$API_IMAGE/{id}")
    fun updateImage(
            @Path("id") id: Int,
            @Query("position") position: Byte,
            @Query("thumbnail") thumbnail: Byte
    ): Call<JsonObject>

    @DELETE("$API_IMAGE/{id}")
    fun deleteImage(@Path("id") id: Int): Call<JsonObject>

    @DELETE("$API_HOUSE/{house_id}/images")
    fun deleteAllImage(@Path("house_id") houseId: Int): Call<JsonObject>
}