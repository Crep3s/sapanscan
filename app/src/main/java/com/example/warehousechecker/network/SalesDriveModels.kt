package com.example.warehousechecker.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// 1. Самый верхний уровень ответа от сервера
data class ApiResponse(
    val status: String,
    val data: OrderData // Вложенный объект "data"
)

// 2. Объект "data", который содержит информацию о сделке
data class OrderData(
    // Здесь могут быть другие поля сделки, но нам нужен только список продуктов
    @SerializedName("products") val productList: List<Product>
)

// 3. Модель самого продукта (она остается почти без изменений)
@Parcelize
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    @SerializedName("bar_code") val barCode: String?,
    @SerializedName("articul") val sku: String?
) : Parcelable