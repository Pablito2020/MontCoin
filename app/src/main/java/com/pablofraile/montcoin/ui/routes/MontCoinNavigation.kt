package com.pablofraile.montcoin.ui.routes

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


/**
 * Destinations used in the [MontCoinApp].
 */
object MontCoinDestinations {
    const val OPERATION_ROUTE = "operation"
    const val TRANSACTIONS_ROUTE = "transactions"
    const val WATCH_INCOMES = "incomes"
}

/**
 * Models the navigation actions in the app.
 */
class MontCoinNavigationActions(navController: NavHostController) {
    val navigateToOperation: () -> Unit = {
        navController.navigate(MontCoinDestinations.OPERATION_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    val navigateToTransactions: () -> Unit = {
        navController.navigate(MontCoinDestinations.TRANSACTIONS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToIncomes: () -> Unit = {
        navController.navigate(MontCoinDestinations.WATCH_INCOMES) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}