import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MarketPriceViewModelFactory(
    private val repository: MarketPriceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketPriceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketPriceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

