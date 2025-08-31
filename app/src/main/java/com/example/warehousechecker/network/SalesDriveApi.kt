import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SalesDriveApi {
    @GET("deals/{id}")
    suspend fun getDealById(
        @Path("id") dealId: String,
        @Query("api_key") apiKey: String
    ): SalesDriveResponse
}