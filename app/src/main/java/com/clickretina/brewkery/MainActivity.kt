package com.clickretina.brewkery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.clickretina.brewkery.ui.navigation.BrewkeryNavGraph
import com.clickretina.brewkery.ui.theme.BrewkeryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as BrewkeryApplication).container

        setContent {
            BrewkeryTheme {
                val navController = rememberNavController()
                BrewkeryNavGraph(
                    navController = navController,
                    repository = appContainer.repository
                )
            }
        }
    }
}
