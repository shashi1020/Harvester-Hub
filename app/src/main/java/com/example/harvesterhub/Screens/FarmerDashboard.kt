package com.example.farmerdashboard.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.painterResource
import com.example.harvesterhub.R
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.harvesterhub.utils.RequestAndFetchLocation


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FarmerDashboard(navController: NavController) {
    val context = LocalContext.current

    val dashboardItems = listOf(
        "📈 बाजारभाव" to "marketPrices",
        "🛒 खरेदीदारांची यादी" to "buyerListings",
        "🤝 गट विक्री केंद्र" to "groupSelling",
        "📊 विक्री विश्लेषण" to "salesInsights"
    )

    val connectedFarmers = listOf(
        "राम पाटील - आंबा - २० पेटी",
        "सीमा गावित - संत्री - १५ पेटी",
        "विजय मोरे - द्राक्षे - १० पेटी",
        "अनिता शिंदे - केळी - २५ पेटी"
    )

    Scaffold(
        containerColor = Color(0xFFF8FAF0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "डॅशबोर्ड",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* drawer or back */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "मेनू")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "सूचना")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "सेटिंग्ज")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            RequestAndFetchLocation { lat, lng, state, district ->
                // Location handling logic
            }

            // Grid Items with enhanced design
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                items(dashboardItems) { (label, route) ->
                    DashboardItem(label) { navController.navigate(route) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // चालू विक्री Section with enhanced styling

        }
    }
}

@Composable
fun DashboardItem(label: String, onClick: () -> Unit) {
    val gradientColors = when (label.substringAfter(" ")) {
        "बाजारभाव" -> listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
        "खरेदीदारांची यादी" -> listOf(Color(0xFF2196F3), Color(0xFF42A5F5))
        "गट विक्री केंद्र" -> listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
        "विक्री विश्लेषण" -> listOf(Color(0xFF9C27B0), Color(0xFFBA68C8))
        else -> listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.3f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradientColors),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label.take(2), // Emoji
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = label.substringAfter(" "),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun prev() {
    val navController = rememberNavController()
    FarmerDashboard(navController)
}