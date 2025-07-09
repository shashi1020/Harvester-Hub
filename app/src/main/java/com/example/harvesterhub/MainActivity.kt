package com.example.harvesterhub


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import com.example.harvesterhub.Screens.AppNavigator
import com.example.harvesterhub.Screens.CommodityPriceTable
import com.example.harvesterhub.data.models.MarketPriceEntity
import com.google.firebase.FirebaseApp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "App crashed", throwable)
        }

        setContent {
            val context = LocalContext.current
            FirebaseApp.initializeApp(context)
//            val dummyPrices = listOf(
//                MarketPriceEntity(
//                    id = 1,
//                    state = "Uttar Pradesh",
//                    district = "Hathras",
//                    market = "Shadabad",
//                    commodity = "Tomato",
//                    variety = "Deshi",
//                    grade = "FAQ",
//                    arrivalDate = "18/06/2025",
//                    minPrice = "1400",
//                    maxPrice = "1500",
//                    modalPrice = "1450",
//                    commodityCode = "78"
//                ),
//                MarketPriceEntity(
//                    id = 2,
//                    state = "Uttar Pradesh",
//                    district = "Hathras",
//                    market = "Mandi A",
//                    commodity = "Tomato",
//                    variety = "Hybrid",
//                    grade = "A",
//                    arrivalDate = "18/06/2025",
//                    minPrice = "1300",
//                    maxPrice = "1400",
//                    modalPrice = "1350",
//                    commodityCode = "78"
//                ),
//                MarketPriceEntity(
//                    id = 3,
//                    state = "Uttar Pradesh",
//                    district = "Hathras",
//                    market = "Mandi B",
//                    commodity = "Banana",
//                    variety = "Desi",
//                    grade = "A",
//                    arrivalDate = "18/06/2025",
//                    minPrice = "1000",
//                    maxPrice = "1100",
//                    modalPrice = "1050",
//                    commodityCode = "79"
//                )
//            )
//
//            CommodityPriceTable(prices = dummyPrices, districtQuery = "Hathras")
            AppNavigator()
        }



    }

}



