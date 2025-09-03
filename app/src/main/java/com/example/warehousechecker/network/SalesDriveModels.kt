package com.example.warehousechecker.network
import com.google.gson.annotations.SerializedName

// Модель для полного ответа от API
data class SalesDriveResponse(
    val data: DealData
)

// Модель, описывающая сам заказ (сделку)
data class DealData(
    val id: String,
    @SerializedName("products") // Указываем, что в JSON поле называется "products"
    val productList: List<Product>
)

// Модель для одного товара в заказе
data class Product(
    val name: String,
    val sku: String?, // Артикул может отсутствовать
    val quantity: Double
)