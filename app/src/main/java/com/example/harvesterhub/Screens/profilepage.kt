package com.example.harvesterhub.Screens

// --- IMPORTS ---
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person // For profile
import androidx.compose.material.icons.filled.Groups // For connected farmers
import androidx.compose.material.icons.filled.Storefront // For merchant offers
import androidx.compose.material.icons.automirrored.filled.TrendingUp // For sales history
import androidx.compose.material.icons.filled.Star // For rating bar
import androidx.compose.material.icons.filled.RateReview // For feedbacks
import androidx.compose.material3.* // Material 3 components
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // For passing icons to InsightSection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.harvesterhub.viewModel.FarmerViewModel
import com.google.firebase.auth.FirebaseAuth


import androidx.compose.material.icons.filled.AccountCircle // For a prominent profile avatar
import androidx.compose.material.icons.filled.LocationOn // For location
import androidx.compose.material.icons.filled.LocalFlorist // Or Agriculture for crops, if LocalFlorist is preferred visually



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesInsightsScreen(viewModel: FarmerViewModel = viewModel()) {
    // Replaced hardcoded colors with MaterialTheme's color scheme
    val harvesterPrimary = Color(0xFF4B5D2A) // A deep, earthy green
    val harvesterOnPrimary = Color(0xFFFDFBF7) // Off-white for text on primary
    val harvesterBackground = Color(0xFFF0F4E8) // A light, airy background
    val harvesterSurface = Color(0xFFFFFFFF) // White for cards
    val harvesterOnSurface = Color(0xFF2C3524) // Dark text on light surfaces
    val harvesterSurfaceVariant = Color(0xFFE0E5D7) // Lighter card background for reviews
    val harvesterOnSurfaceVariant = Color(0xFF4B5D2A) // Text on surface variant
    val harvesterAccent = Color(0xFF8BC34A) // A brighter green for accents (like progress/active states)



    // Custom ColorScheme if not using a full MaterialTheme setup
    val customColorScheme = lightColorScheme(
        primary = harvesterPrimary,
        onPrimary = harvesterOnPrimary,
        background = harvesterBackground,
        onBackground = harvesterOnSurface,
        surface = harvesterSurface,
        onSurface = harvesterOnSurface,
        surfaceVariant = harvesterSurfaceVariant,
        onSurfaceVariant = harvesterOnSurfaceVariant,
        error = Color(0xFFBA1A1A), // Standard error red
        onError = Color(0xFFFFFFFF),
        tertiary = harvesterAccent // Using tertiary for accents
    )

    MaterialTheme(colorScheme = customColorScheme) {
        val farmerState by viewModel.farmerState.collectAsState()
        val cachedFarmerState = remember(farmerState) { farmerState }
        val activeGroups = viewModel.myActiveGroups

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        LaunchedEffect(Unit) {
            if (uid != null) {
                Log.d("SalesInsightsScreen", "Calling fetchFarmerByPhone with $uid")
                viewModel.fetchFarmerByPhone(uid)
            } else {
                Log.e("SalesInsightsScreen", "Phone number is null, cannot fetch farmer data")
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Sales Insights",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (uid == null) {
                    Text(
                        "User phone number not found. Please login again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center // Fixed this line
                    )
                } else if (cachedFarmerState != null) {
                    // --- PROFILE CARD: BINANCE-LIKE DESIGN ---
                    // Using InsightSection but customizing its content heavily
                    InsightSection(title = "Farmer Profile", icon = Icons.Default.Person) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            // Top part: Avatar, Name, Location
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Prominent Profile Icon (Avatar Placeholder)
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Farmer Avatar",
                                    tint = MaterialTheme.colorScheme.primary, // Use primary color for the avatar
                                    modifier = Modifier
                                        .size(64.dp) // Larger size for prominence
                                        .padding(end = 12.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cachedFarmerState.fullName,
                                        style = MaterialTheme.typography.headlineSmall, // Larger, more prominent name
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Location",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = cachedFarmerState.district +", " + cachedFarmerState.state,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Horizontal Divider for separation
                            Divider(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // Crops Section - made more prominent
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFlorist, // Or Icons.Default.Agriculture
                                        contentDescription = "Crops",
                                        tint = MaterialTheme.colorScheme.tertiary, // Use accent color for crops icon
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Primary Crops:",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
//                                Text(
//                                    text = farmerState!!."",
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    fontWeight = FontWeight.Medium,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
                            }

                            // Placeholder for "Stats" or "Quick Info" (visually appealing, no real data change)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                StatItem(label = "Total Sales", value = "₹23K", icon = Icons.AutoMirrored.Filled.TrendingUp) // Placeholder
                                StatItem(label = "Ratings", value = "4.5 / 5", icon = Icons.Default.Star) // Placeholder
                                StatItem(label = "Active Listings", value = "3", icon = Icons.Default.Storefront) // Placeholder
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 32.dp)
                    )
                }

                InsightSection(title = "My Active Groups", icon = Icons.Default.Groups) {
                    if (activeGroups.isEmpty()) {
                        Text(
                            "You have no active groups.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeGroups.forEach { group ->
                                Column {
                                    Text(
                                        "• ${group.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "   Commodity: ${group.commodity}, Qty: ${group.quantity}kg",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                   Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }

                // MERCHANT OFFERS
                InsightSection(title = "Merchant Offers", icon = Icons.Default.Storefront) {
                    Text("• AgroFresh Traders – ₹15/kg (Onion)", style = MaterialTheme.typography.bodyMedium)
                    Text("• GreenBasket Co. – ₹10/kg (Tomato)", style = MaterialTheme.typography.bodyMedium)
                }

                // SALES HISTORY
                InsightSection(title = "Sales History", icon = Icons.AutoMirrored.Filled.TrendingUp) {
                    Text("• Onion – 1000 kg → ₹15,000", style = MaterialTheme.typography.bodyMedium)
                    Text("• Tomato – 800 kg → ₹8,000", style = MaterialTheme.typography.bodyMedium)
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        "📊 March Total: ₹23,000",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // FEEDBACKS / REVIEWS
                InsightSection(title = "Feedbacks", icon = Icons.Default.RateReview) {
                    val reviews = listOf(
                        Review("Rajesh Pawar (Farmer)", "Great collaboration and timely payments.", 4),
                        Review("Anita More (Farmer)", "Very reliable and cooperative.", 5),
                        Review("AgroFresh Traders (Merchant)", "Consistent quality produce delivered.", 4),
                        Review("GreenBasket Co. (Merchant)", "Looking forward to more business.", 3)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        reviews.forEach { review ->
                            ReviewItem(review)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Data classes (unchanged)
data class Review(val reviewer: String, val comment: String, val rating: Int)

// StatItem Composable (New for Profile Card)
@Composable
fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary, // Accent color for stat icons
            modifier = Modifier.size(28.dp) // Larger icon
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium, // Prominent value
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall, // Smaller label
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = review.reviewer,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\"${review.comment}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            RatingBar(rating = review.rating)
        }
    }
}

@Composable
fun RatingBar(rating: Int, maxRating: Int = 5) {
    Row {
        for (i in 1..maxRating) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = if (i <= rating) "Filled star" else "Empty star",
                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun InsightSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title section icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Divider(
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            content()
        }
    }
}

@Composable
fun InsightItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}