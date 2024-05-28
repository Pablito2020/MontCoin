package com.pablofraile.montcoin.ui.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pablofraile.montcoin.data.AppContainer
import com.pablofraile.montcoin.ui.operation.OperationRoute
import com.pablofraile.montcoin.ui.operation.OperationViewModel
import com.pablofraile.montcoin.ui.operations.OperationsRoute
import com.pablofraile.montcoin.ui.operations.OperationsViewModel
import com.pablofraile.montcoin.ui.write.WriteCardRoute
import com.pablofraile.montcoin.ui.write.WriteCardViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MontCoinNavGraph(
    container: AppContainer,
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
            val model: OperationViewModel = viewModel(
                factory = OperationViewModel.provideFactory(
                    container.cardRepository,
                    container.operationsRepository
                )
            )
            OperationRoute(
                model = model,
                openDrawer = openDrawer,
            )
        }
        composable(route = MontCoinDestinations.OPERATIONS_ROUTE) { navBackStackEntry ->
            val model: OperationsViewModel =
                viewModel(factory = OperationsViewModel.provideFactory(container.operationsRepository))
            OperationsRoute(
                model = model,
                openDrawer = openDrawer,
            )
        }
        composable(route = MontCoinDestinations.WRITE_CARD) { navBackStackEntry ->
            val model: WriteCardViewModel =
                viewModel(factory = WriteCardViewModel.provideFactory(container.cardRepository, container.usersRepository))
            WriteCardRoute(model = model, openDrawer = openDrawer)
        }
    }
}