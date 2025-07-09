import android.content.Context
import com.example.harvesterhub.data.models.MarketPriceEntity
import com.example.harvesterhub.data.repository.toEntity
import com.example.harvesterhub.roomDB.AppDatabase
import com.example.harvesterhub.utils.saveLastFetchDate
import java.util.Calendar

class MarketPriceRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.marketPriceDao()
    suspend fun clearDatabase() {
        dao.clearAllMarketPrices()

    }

    suspend fun refreshMarketPrices(apiKey: String) {
        val sdf = java.text.SimpleDateFormat("dd-MM-yyyy")
        val today = sdf.format(java.util.Date())

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)  // Subtract 1 day to get yesterday
        val yesterday = sdf.format(calendar.time)


        // 2. Fetch today's data from API
        val apiResponse = RetrofitInstance.api.getMarketPrices(
            apiKey = apiKey,
            date = yesterday // only today's data
        )
        saveLastFetchDate(context,today)

        // 3. Convert and insert into DB
        val marketPriceEntities = apiResponse.records.map { it.toEntity() }
        dao.insertAll(marketPriceEntities)
    }


    suspend fun getLast7DaysPrices(): List<MarketPriceEntity> {
        val sdf = java.text.SimpleDateFormat("dd-MM-yyyy")
        val calendar = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_YEAR, -7)
        }

        return dao.getAllPrices()
    }

    suspend fun getTodayPrices(): List<MarketPriceEntity> {
        return dao.getAllPrices() // since only today's data is stored
    }

}
