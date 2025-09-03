package com.example.warehousechecker.data
import android.content.Context
import com.example.warehousechecker.network.Product
import com.example.warehousechecker.network.RetrofitClient
import com.example.warehousechecker.BuildConfig
class WarehouseRepository(context: Context) {
    private val salesDriveApi = RetrofitClient.instance
    private val sheetsHelper = GoogleSheetsHelper(context)

    // ЗАМЕНИТЕ на ваш API ключ
    private val apiKey = BuildConfig.SALESDRIVE_API_KEY

    suspend fun getOrderProducts(orderId: String): Result<List<Product>> {
        return try {
            val response = salesDriveApi.getDealById(orderId, apiKey)
            // ИЗМЕНЕНИЕ: Правильно извлекаем список продуктов из вложенной структуры
            if (response.status == "ok" && response.data.productList.isNotEmpty()) {
                Result.success(response.data.productList)
            } else {
                Result.failure(Exception("Заказ не найден или в нем нет товаров"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getSkuForBarcode(barcode: String): Result<String?> {
        return try {
            val sku = sheetsHelper.findSkuByBarcode(barcode)
            Result.success(sku)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}