import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "records")
    val records: List<MarketPriceResponse>
)
@JsonClass(generateAdapter = true)
data class MarketPriceResponse(

    @Json(name = "State") val state: String,
    @Json(name = "District") val district: String,
    @Json(name = "Market") val market: String,
    @Json(name = "Commodity") val commodity: String,
    @Json(name = "Variety") val variety: String,
    @Json(name = "Grade") val grade: String,
    @Json(name = "Arrival_Date") val arrivalDate: String,
    @Json(name = "Min_Price") val minPrice: String,
    @Json(name = "Max_Price") val maxPrice: String,
    @Json(name = "Modal_Price") val modalPrice: String,
    @Json(name = "Commodity_Code") val commodityCode: String
) {
    // 🆕 Virtual field (not from API): helps in location-based search
    val location: String
        get() = "$state $district"
}
