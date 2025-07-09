import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harvesterhub.data.repository.toResponse

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MarketPriceViewModel(private val repository: MarketPriceRepository) : ViewModel() {

    private val _prices = MutableStateFlow<List<MarketPriceResponse>>(emptyList())
    val prices: StateFlow<List<MarketPriceResponse>> = _prices

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun clearMarketPrices() {
        viewModelScope.launch {
            repository.clearDatabase()
        }
    }
    fun loadPricesFromDB() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val localData = repository.getTodayPrices()
                _prices.value = localData.map { it.toResponse() }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load prices from DB: ${e.message}"
            }
            _isLoading.value = false
        }
    }



    fun loadPrices(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Refresh from API & save to Room
                repository.refreshMarketPrices(apiKey)

                // Load fresh data from DB
                val localData = repository.getLast7DaysPrices()
                _prices.value = localData.map { it.toResponse() }

                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load market prices: ${e.message}"

                // Optional fallback: load stale data from Room if API fails
                val fallback = repository.getLast7DaysPrices()
                if (fallback.isNotEmpty()) {
                    _prices.value = fallback.map { it.toResponse() }
                }
            }
            _isLoading.value = false
        }
    }
}

