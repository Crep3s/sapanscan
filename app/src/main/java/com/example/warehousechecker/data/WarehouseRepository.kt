package com.example.warehousechecker.data
import android.content.Context
import com.example.warehousechecker.network.Product
import com.example.warehousechecker.network.RetrofitClient

class WarehouseRepository(context: Context) {
    private val salesDriveApi = RetrofitClient.instance
    private val sheetsHelper = GoogleSheetsHelper(context)

    // ЗАМЕНИТЕ на ваш API ключ
    private val apiKey = "ВАШ_API_КЛЮЧ_SALESDRIVE"

    suspend fun getOrderProducts(orderId: String): Result<List<Product>> {
        return try {
            val response = salesDriveApi.getDealById(orderId, apiKey)
            Result.success(response.data.productList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSkuForBarcode(barcode: String): Result<String?> {
        return try {
            val sku = sheetsHelper.findSkuByBarcode(barcode)
            Result.success(sku)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}