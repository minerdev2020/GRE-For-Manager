package com.minerdev.greformanager.retrofit

import com.google.gson.JsonElement
import com.minerdev.greformanager.model.House
import com.minerdev.greformanager.model.Image
import com.minerdev.greformanager.utils.Constants.API_HOUSE
import com.minerdev.greformanager.utils.Constants.API_IMAGE
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {
    // 네이버 지도 서비스
    @GET(API_HOUSE)
    fun getQueryResponseFromNaver(@Query("query") address: String): Call<JsonElement>

    // 매물 정보
    @GET(API_HOUSE)
    fun getAllHouse(@Query("last_updated_at") lastUpdatedAt: String): Call<JsonElement>

    @GET("$API_HOUSE/{id}")
    fun getHouse(@Path("id") id: Int): Call<JsonElement>

    @POST(API_HOUSE)
    fun createHouse(@Body house: House): Call<JsonElement>

    @PATCH("$API_HOUSE/{id}")
    fun updateHouse(@Path("id") id: Int, @Body house: House): Call<JsonElement>

    @DELETE("$API_HOUSE/{id}")
    fun deleteHouse(@Path("id") id: Int): Call<JsonElement>

    @DELETE("$API_HOUSE/{id}")
    fun deleteAllHouse(@Path("id") id: Int): Call<JsonElement>


    // 매물 이미지 정보
    @GET("$API_HOUSE/{house_id}/images")
    fun getAllImage(@Path("house_id") houseId: Int): Call<JsonElement>

    @GET("$API_HOUSE/{house_id}/images/{position}")
    fun getImage(@Path("house_id") houseId: Int, @Path("position") position: Int): Call<JsonElement>

    @Multipart
    @POST("$API_HOUSE/{house_id}/images")
    fun createImage(@Path("house_id") houseId: Int, @Body image: Image, @Part("photo") imageFile: RequestBody): Call<JsonElement>

    @PATCH("$API_IMAGE/{id}")
    fun updateImage(@Path("id") id: Int, @Body image: Image): Call<JsonElement>

    @DELETE("$API_IMAGE/{id}")
    fun deleteImage(@Path("id") id: Int): Call<JsonElement>

    @DELETE("$API_HOUSE/{house_id}/images")
    fun deleteAllImage(@Path("house_id") houseId: Int): Call<JsonElement>
}