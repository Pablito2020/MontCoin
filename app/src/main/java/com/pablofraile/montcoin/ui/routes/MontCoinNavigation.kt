package com.pablofraile.montcoin.ui.routes

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


/**
 * Destinations used in the [MontCoinApp].
 */
object MontCoinDestinations {
    const val OPERATION_ROUTE = "operation"
    const val OPERATIONS_ROUTE = "operations"
    const val WRITE_CARD = "write_card"
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
    val navigateToOperations: () -> Unit = {
        navController.navigate(MontCoinDestinations.OPERATIONS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToWriteCard: () -> Unit = {
        navController.navigate(MontCoinDestinations.WRITE_CARD) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}