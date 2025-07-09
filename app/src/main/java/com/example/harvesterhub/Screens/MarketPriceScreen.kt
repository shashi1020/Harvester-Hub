package com.example.harvesterhub.Screens

// --- IMPORTS ---
import MarketPriceRepository
import MarketPriceResponse
import MarketPriceViewModel
import MarketPriceViewModelFactory
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.harvesterhub.utils.RequestAndFetchLocation
import com.example.harvesterhub.utils.getLastFetchDate
import com.example.harvesterhub.utils.saveLastFetchDate

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data classes (add these if they don't exist)
data class ApiResponse(
    val commodity: String,
    val variety: String,
    val grade: String,
    val arrivalDate: String,
    val market: String,
    val state: String,
    val district: String,
    val modalPrice: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPriceScreen() {
    // --- Material 3 Color Scheme ---
    val harvesterPrimary = Color(0xFF4B5D2A)
    val harvesterOnPrimary = Color(0xFFFDFBF7)
    val harvesterBackground = Color(0xFFF0F4E8)
    val harvesterSurface = Color(0xFFFFFFFF)
    val harvesterOnSurface = Color(0xFF2C3524)
    val harvesterSurfaceVariant = Color(0xFFE0E5D7)
    val harvesterOnSurfaceVariant = Color(0xFF4B5D2A)
    val harvesterAccent = Color(0xFF8BC34A)
    val harvesterError = Color(0xFFBA1A1A)

    val customColorScheme = lightColorScheme(
        primary = harvesterPrimary,
        onPrimary = harvesterOnPrimary,
        background = harvesterBackground,
        onBackground = harvesterOnSurface,
        surface = harvesterSurface,
        onSurface = harvesterOnSurface,
        surfaceVariant = harvesterSurfaceVariant,
        onSurfaceVariant = harvesterOnSurfaceVariant,
        error = harvesterError,
        onError = Color.White,
        tertiary = harvesterAccent,
        errorContainer = harvesterError.copy(alpha = 0.12f),
        onErrorContainer = harvesterError,
        outline = Color(0xFF79747E),
        outlineVariant = Color(0xFFCAC4D0)
    )

    MaterialTheme(colorScheme = customColorScheme) {
        val context = LocalContext.current
        val repository = MarketPriceRepository(context)
        val viewModel: MarketPriceViewModel = viewModel(
            factory = MarketPriceViewModelFactory(repository)
        )

        var commodityQuery by remember { mutableStateOf("") }
        var marketQuery by remember { mutableStateOf("") }
        var stateQuery by remember { mutableStateOf("") }
        var districtQuery by remember { mutableStateOf("") }

        var locationFetched by remember { mutableStateOf(false) }
        var userState by remember { mutableStateOf("Fetching location...") }
        var userDistrict by remember { mutableStateOf("") }

        val keyboardController = LocalSoftwareKeyboardController.current


        var showFilterDialog by remember { mutableStateOf(false) }

        val prices by viewModel.prices.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        // Location fetching
        if (!locationFetched) {
            RequestAndFetchLocation { _, _, state, district ->
                state?.let {
                    stateQuery = it
                    userState = it
                }
                district?.let {
                    districtQuery = it
                    userDistrict = it
                }
                locationFetched = true
                if (state == null && district == null) {
                    userState = "Location not found"
                    userDistrict = ""
                }
            }
        }




        // Data fetching




        LaunchedEffect(Unit) {

            val lastFetchDate = getLastFetchDate(context)
            val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)


            if (lastFetchDate != today) {
                // It's a new day, fetch fresh data
//                viewModel.loadPrices("579b464db66ec23bdd000001dd9754baa209481c724a3f750b2ea523")
            }
//            viewModel.loadPricesFromDB()
            viewModel.clearMarketPrices()

        }


        // Filtering Logic
        val filteredPrices = remember(prices, commodityQuery, marketQuery, stateQuery) {
            prices.filter { price ->
                price.commodity.contains(commodityQuery, ignoreCase = true) &&
                        (marketQuery.isBlank() || price.market.contains(marketQuery, ignoreCase = true)) &&
                        (stateQuery.isBlank() || price.state.contains(stateQuery, ignoreCase = true))
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Market Pulse",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { /* Handle share action */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Hello Farmer!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Today's Market Insight",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Your Location",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userDistrict.isNotBlank()) "$userDistrict, $userState" else userState,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // Search Section
                Spacer(modifier = Modifier.height(16.dp))

                // Simple search input instead of SearchBar
                OutlinedTextField(
                    value = commodityQuery,
                    onValueChange = { commodityQuery = it },
                    placeholder = { Text("Search for your crop (e.g., Onion, Wheat)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                    trailingIcon = {
                        if (commodityQuery.isNotEmpty()) {
                            IconButton(onClick = { commodityQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(28.dp)
                )

                // Filter Button
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.primary)
                    ),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Advanced Filters", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Advanced Filters", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content Section
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        "Fetching the latest market data...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (error != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "⚠️ Error: $error",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Text(
                        text = "📦 ${filteredPrices.size} results found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )

                    if (filteredPrices.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "No results",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No market data found for your filters.\nTry adjusting your search or filters.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredPrices) { price ->
                                MarketPriceItemCard(price = price)
                            }
                        }
                    }
                }
            }

            // Filter Dialog
            if (showFilterDialog) {
                AlertDialog(
                    onDismissRequest = { showFilterDialog = false },
                    title = { Text("Advanced Filters") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = marketQuery,
                                onValueChange = { marketQuery = it },
                                label = { Text("Market (Optional)") },
                                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = "Market") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = stateQuery,
                                onValueChange = { stateQuery = it },
                                label = { Text("State (Optional)") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "State") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = districtQuery,
                                onValueChange = { districtQuery = it },
                                label = { Text("District (Optional)") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "District") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showFilterDialog = false }) {
                            Text("Apply")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFilterDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MarketPriceItemCard(price: MarketPriceResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp).wrapContentHeight()) {
            // Market & Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = price.market,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${price.district.trim()}, ${price.state.trim()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Commodity Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Crop,
                    contentDescription = "Commodity",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${price.commodity.trim()} (${price.variety.trim()})",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
//                if (price.grade.isNotBlank()) {
//                    Text(
//                        text = " - Grade: ${price.grade.trim()}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Modal Price:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${price.modalPrice} / 100kg",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val pricePerKg = price.modalPrice.toDoubleOrNull()?.div(100.0) ?: 0.0
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Approx. per kg:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹%.2f".format(pricePerKg),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arrival Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Arrival Date",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Arrival Date: ${price.arrivalDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}