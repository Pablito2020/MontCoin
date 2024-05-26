package com.pablofraile.montcoin.ui.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pablofraile.montcoin.nfc.NfcActivityTemplate
import com.pablofraile.montcoin.ui.operation.OperationRoute
import com.pablofraile.montcoin.ui.operation.OperationViewModel
import com.pablofraile.montcoin.ui.transactions.TransactionsRoute
import com.pablofraile.montcoin.ui.transactions.TransactionsScreen
import com.pablofraile.montcoin.ui.transactions.TransactionsViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MontCoinNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = MontCoinDestinations.OPERATION_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = MontCoinDestinations.OPERATION_ROUTE) { navBackStackEntry ->
            val flow = (LocalContext.current as NfcActivityTemplate).data
            val model: OperationViewModel =
                viewModel(factory = OperationViewModel.provideFactory(flow))
            OperationRoute(
                model = model,
                openDrawer = openDrawer,
            )
        }
        composable(route = MontCoinDestinations.TRANSACTIONS_ROUTE) { navBackStackEntry ->
            val model: TransactionsViewModel = viewModel(factory = TransactionsViewModel.provideFactory())
            TransactionsRoute(
                model = model,
                openDrawer = openDrawer,
            )
        }
    }
}