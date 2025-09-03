package com.example.warehousechecker.network
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
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

@Parcelize // <-- ДОБАВЬТЕ ЭТУ АННОТАЦИЮ
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    @SerializedName("bar_code") val barCode: String?,
    @SerializedName("articul") val sku: String?
) : Parcelable