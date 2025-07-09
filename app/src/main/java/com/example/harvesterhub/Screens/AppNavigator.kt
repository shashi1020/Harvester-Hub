package com.example.harvesterhub.Screens


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmerdashboard.ui.FarmerDashboard
import com.example.harvesterhub.viewModel.FarmerViewModel
import com.google.firebase.auth.FirebaseAuth

@ExperimentalMaterial3Api
@Composable
fun AppNavigator() {
    val navController: NavHostController = rememberNavController()
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) "dashboard" else "login"
    ) {
        composable("OnboardingScreen") { OnboardingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("dashboard") { FarmerDashboard(navController) }
        composable("marketPrices") { MarketPriceScreen() }
        composable("buyerListings") { BuyerListingsScreen() }
        composable("groupSelling") { GroupSellingScreen() }
        composable("salesInsights") { backStackEntry ->
            val viewModel: FarmerViewModel = viewModel(backStackEntry)
            SalesInsightsScreen(viewModel)
        }
        composable("notifications") { NotificationsScreen() }
        composable("settings") { SettingsScreen() }
    }
}
