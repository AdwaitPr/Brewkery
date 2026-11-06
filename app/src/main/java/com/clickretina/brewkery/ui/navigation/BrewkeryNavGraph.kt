package com.clickretina.brewkery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.ui.screens.cart.CartScreen
import com.clickretina.brewkery.ui.screens.cart.CartViewModel
import com.clickretina.brewkery.ui.screens.detail.ItemDetailScreen
import com.clickretina.brewkery.ui.screens.detail.ItemDetailViewModel
import com.clickretina.brewkery.ui.screens.menu.MenuScreen
import com.clickretina.brewkery.ui.screens.menu.MenuViewModel
import com.clickretina.brewkery.ui.screens.order.OrderStatusScreen

sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Detail : Screen("detail/{itemId}") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
    data object Cart : Screen("cart")
    data object OrderStatus : Screen("order_status")
}

@Composable
fun BrewkeryNavGraph(
    navController: NavHostController,
    repository: BrewkeryRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(Screen.Menu.route) {
            val menuViewModel: MenuViewModel = viewModel(
                factory = MenuViewModel.provideFactory(repository)
            )
            MenuScreen(
                viewModel = menuViewModel,
                onNavigateToDetail = { itemId ->
                    navController.navigate(Screen.Detail.createRoute(itemId))
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToOrder = {
                    navController.navigate(Screen.OrderStatus.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 1
            val detailViewModel: ItemDetailViewModel = viewModel(
                key = "item_$itemId",
                factory = ItemDetailViewModel.provideFactory(itemId, repository)
            )
            ItemDetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route) {
                        popUpTo(Screen.Menu.route)
                    }
                }
            )
        }

        composable(Screen.Cart.route) {
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModel.provideFactory(repository)
            )
            CartScreen(
                viewModel = cartViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderPlaced = {
                    navController.navigate(Screen.OrderStatus.route) {
                        popUpTo(Screen.Menu.route)
                    }
                }
            )
        }

        composable(Screen.OrderStatus.route) {
            OrderStatusScreen(
                repository = repository,
                onBackToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
