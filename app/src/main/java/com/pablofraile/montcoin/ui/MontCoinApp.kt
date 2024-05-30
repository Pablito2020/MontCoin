package com.pablofraile.montcoin.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pablofraile.montcoin.data.AppContainer
import com.pablofraile.montcoin.ui.routes.AppDrawer
import com.pablofraile.montcoin.ui.routes.MontCoinDestinations
import com.pablofraile.montcoin.ui.routes.MontCoinNavGraph
import com.pablofraile.montcoin.ui.routes.MontCoinNavigationActions
import com.pablofraile.montcoin.ui.theme.MontCoinTheme
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MontCoinApp(container: AppContainer) {
    MontCoinTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            MontCoinNavigationActions(navController)
        }

        val coroutineScope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: MontCoinDestinations.OPERATION_ROUTE
        val sizeAwareDrawerState = rememberDrawerState(DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToOperation = navigationActions.navigateToOperation,
                    navigateToTransactions = navigationActions.navigateToOperations,
                    navigateToWriteCard = navigationActions.navigateToWriteCard,
                    navigateToListUsers = navigationActions.navigateToListUsers,
                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } }
                )
            },
            drawerState = sizeAwareDrawerState,
            gesturesEnabled = true
        ) {
            Row {
                MontCoinNavGraph(
                    container = container,
                    navController = navController,
                    openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                )
            }
        }
    }
}
