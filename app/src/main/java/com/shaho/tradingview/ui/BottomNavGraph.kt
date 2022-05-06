package com.shaho.tradingview.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shaho.tradingview.ui.first.FirstFragmentComposable
import com.shaho.tradingview.ui.second.SecondFragmentComposable
import com.shaho.tradingview.ui.third.compose.ThirdFragmentComposable

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            FirstFragmentComposable()
        }
        composable(route = BottomBarScreen.Profile.route) {
            SecondFragmentComposable()
        }
        composable(route = BottomBarScreen.Settings.route) {
            ThirdFragmentComposable()
        }
    }
}
