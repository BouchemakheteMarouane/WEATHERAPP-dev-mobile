package com.bousmah.meteoapp_marouane

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.bousmah.meteoapp_marouane.presentation.ChatScreen
import com.bousmah.meteoapp_marouane.presentation.MapScreen
import com.bousmah.meteoapp_marouane.presentation.SkyDetectionScreen
import com.bousmah.meteoapp_marouane.presentation.SuggestionsScreen
import com.bousmah.meteoapp_marouane.presentation.WeatherScreen
import com.bousmah.meteoapp_marouane.presentation.WeatherViewModel
import com.bousmah.meteoapp_marouane.ui.theme.MeteoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.loadWeatherInfo()
        } else {
            viewModel.loadWeatherInfo("Casablanca")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        setContent {
            MeteoAppTheme {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = "weather") {
                            composable("weather") {
                                WeatherScreen(
                                    viewModel = viewModel,
                                    onOpenMap = { lat, lng, city, temp ->
                                        navController.navigate("map/$lat/$lng/${Uri.encode(city)}/$temp")
                                    },
                                    onOpenChat = { city, temp, description ->
                                        navController.navigate("chat/${Uri.encode(city)}/$temp/${Uri.encode(description)}")
                                    },
                                    onOpenSuggestions = { city, temp, description, humidity, windSpeed, uvIndex ->
                                        navController.navigate(
                                            "suggestions/${Uri.encode(city)}/$temp/${Uri.encode(description)}/$humidity/$windSpeed/$uvIndex"
                                        )
                                    },
                                    onOpenSkyDetection = {
                                        navController.navigate("sky_detection")
                                    }
                                )
                            }
                            composable(
                                route = "map/{lat}/{lng}/{city}/{temp}",
                                arguments = listOf(
                                    navArgument("lat") { type = NavType.StringType },
                                    navArgument("lng") { type = NavType.StringType },
                                    navArgument("city") { type = NavType.StringType },
                                    navArgument("temp") { type = NavType.FloatType }
                                )
                            ) { backStackEntry ->
                                val args = backStackEntry.arguments!!
                                MapScreen(
                                    lat = args.getString("lat")!!.toDouble(),
                                    lng = args.getString("lng")!!.toDouble(),
                                    cityName = args.getString("city") ?: "",
                                    temp = args.getFloat("temp").toDouble(),
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "chat/{city}/{temp}/{description}",
                                arguments = listOf(
                                    navArgument("city") { type = NavType.StringType },
                                    navArgument("temp") { type = NavType.FloatType },
                                    navArgument("description") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val args = backStackEntry.arguments!!
                                ChatScreen(
                                    cityName = args.getString("city") ?: "",
                                    temp = args.getFloat("temp").toDouble(),
                                    description = args.getString("description") ?: "",
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "suggestions/{city}/{temp}/{description}/{humidity}/{wind}/{uv}",
                                arguments = listOf(
                                    navArgument("city") { type = NavType.StringType },
                                    navArgument("temp") { type = NavType.FloatType },
                                    navArgument("description") { type = NavType.StringType },
                                    navArgument("humidity") { type = NavType.IntType },
                                    navArgument("wind") { type = NavType.FloatType },
                                    navArgument("uv") { type = NavType.FloatType }
                                )
                            ) { backStackEntry ->
                                val args = backStackEntry.arguments!!
                                SuggestionsScreen(
                                    city = args.getString("city") ?: "",
                                    temp = args.getFloat("temp").toDouble(),
                                    description = args.getString("description") ?: "",
                                    humidity = args.getInt("humidity"),
                                    windSpeed = args.getFloat("wind").toDouble(),
                                    uvIndex = args.getFloat("uv").toDouble(),
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable("sky_detection") {
                                SkyDetectionScreen(
                                    viewModel = hiltViewModel(),
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
