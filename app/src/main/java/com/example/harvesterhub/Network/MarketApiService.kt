import retrofit2.http.GET
import retrofit2.http.Query

interface MarketApiService {
    @GET("resource/35985678-0d79-46b4-9ed6-6f13308a1d24")
    suspend fun getMarketPrices(
        @Query("api-key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("filters[Arrival_Date]") date: String,
        @Query("limit") limit: Int = 50000
    ): ApiResponse
}