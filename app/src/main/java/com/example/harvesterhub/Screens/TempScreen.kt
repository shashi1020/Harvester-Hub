package com.example.harvesterhub.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.harvesterhub.data.models.MarketPriceEntity
import com.example.harvesterhub.utils.getLastFetchDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CommodityPriceTable(
    prices: List<MarketPriceEntity>,
    districtQuery: String
) {
    val groupedByCommodity = prices
        .filter { it.district.equals(districtQuery, ignoreCase = true) }
        .groupBy { it.commodity }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        groupedByCommodity.forEach { (commodity, entries) ->
            item {
                Text(
                    text = commodity,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                ) {
                    Text("Market", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Min", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Max", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Modal", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                }

                // Table Rows
                entries.forEach { price ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                    ) {
                        Text(price.market, Modifier.weight(1f))
                        Text(price.minPrice, Modifier.weight(1f))
                        Text(price.maxPrice, Modifier.weight(1f))
                        Text(price.modalPrice, Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (groupedByCommodity.isEmpty()) {
            item {
                Text("No data available for $districtQuery", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewCommodityPriceTable() {
    val dummyPrices = listOf(
        MarketPriceEntity(
            id = 1,
            state = "Uttar Pradesh",
            district = "Hathras",
            market = "Shadabad",
            commodity = "Tomato",
            variety = "Deshi",
            grade = "FAQ",
            arrivalDate = "18/06/2025",
            minPrice = "1400",
            maxPrice = "1500",
            modalPrice = "1450",
            commodityCode = "78"
        ),
        MarketPriceEntity(
            id = 2,
            state = "Uttar Pradesh",
            district = "Hathras",
            market = "Mandi A",
            commodity = "Tomato",
            variety = "Hybrid",
            grade = "A",
            arrivalDate = "18/06/2025",
            minPrice = "1300",
            maxPrice = "1400",
            modalPrice = "1350",
            commodityCode = "78"
        ),
        MarketPriceEntity(
            id = 3,
            state = "Uttar Pradesh",
            district = "Hathras",
            market = "Mandi B",
            commodity = "Banana",
            variety = "Desi",
            grade = "A",
            arrivalDate = "18/06/2025",
            minPrice = "1000",
            maxPrice = "1100",
            modalPrice = "1050",
            commodityCode = "79"
        )
    )

    CommodityPriceTable(prices = dummyPrices, districtQuery = "Hathras")
}
