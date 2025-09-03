package com.example.warehousechecker.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SalesDriveApi {

    @FormUrlEncoded
    @POST("getDealById")
    suspend fun getDealById(
        @Field("deal_id") orderId: String,
        @Field("api_token") apiKey: String
    ): ApiResponse // <-- ИЗМЕНЕНИЕ: Используем новый класс верхнего уровня
}