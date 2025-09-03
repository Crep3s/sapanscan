package com.example.warehousechecker.data
import android.content.Context
import com.example.warehousechecker.R
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleSheetsHelper(private val context: Context) {
    // ЗАМЕНИТЕ на ID вашей таблицы
    private val spreadsheetId = "ВАШ_ID_ТАБЛИЦЫ"
    // ЗАМЕНИТЕ на имя листа и диапазон (A: штрих-код, B: артикул)
    private val range = "Лист1!A:B"

    private fun getSheetsService(): Sheets {
        val credentialsStream = context.resources.openRawResource(R.raw.credentials)
        val credentials = GoogleCredential.fromStream(credentialsStream)
            .createScoped(listOf(SheetsScopes.SPREADSHEETS_READONLY))

        return Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credentials
        ).setApplicationName("Warehouse Checker").build()
    }

    suspend fun findSkuByBarcode(barcode: String): String? = withContext(Dispatchers.IO) {
        try {
            val service = getSheetsService()
            val response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()

            val values = response.getValues()
            if (values.isNullOrEmpty()) {
                return@withContext null
            }

            for (row in values) {
                val rowBarcode = row.getOrNull(0)?.toString()
                val rowSku = row.getOrNull(1)?.toString()
                if (rowBarcode == barcode) {
                    return@withContext rowSku
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}