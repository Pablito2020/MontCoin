package com.pablofraile.montcoin.ui.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pablofraile.montcoin.data.AppContainer
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.ui.bulk.BulkOperationRoute
import com.pablofraile.montcoin.ui.operation.OperationRoute
import com.pablofraile.montcoin.ui.operation.OperationViewModel
import com.pablofraile.montcoin.ui.operations.OperationsRoute
import com.pablofraile.montcoin.ui.operations.OperationsViewModel
import com.pablofraile.montcoin.ui.user.UserRoute
import com.pablofraile.montcoin.ui.user.UserViewModel
import com.pablofraile.montcoin.ui.users.UsersRoute
import com.pablofraile.montcoin.ui.users.UsersViewModel
import com.pablofraile.montcoin.ui.write.WriteCardRoute
import com.pablofraile.montcoin.ui.write.WriteCardViewModel

const val USER_ID = "userId"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MontCoinNavGraph(
    container: AppContainer,
    modifier: Modifier = Modifier,
    navigationActions: MontCoinNavigationActions,
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
        composable(route = MontCoinDestinations.LIST_USERS) { navBackStackEntry ->
            val model: UsersViewModel = viewModel(factory = UsersViewModel.provideFactory(container.usersRepository))
            UsersRoute(onUserClick = navigationActions.navigateToUser, model = model, openDrawer = openDrawer)
        }
        composable(
            route = "${MontCoinDestinations.USER_ROUTE}/{$USER_ID}",
            arguments = listOf(navArgument(USER_ID) {type = NavType.StringType}),
        ) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString(USER_ID)
            val id = Id(userId!!)
            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModel.provideFactory(
                    userId = id,
                    usersRepository = container.usersRepository,
                    operationsRepository = container.operationsRepository
                )
            )
            UserRoute(
                model = userViewModel,
                onGoBack = navigationActions.navigateBack,
            )
        }
        composable(route = MontCoinDestinations.BULK_OPERATION) { navBackStackEntry ->
            BulkOperationRoute(
                openDrawer = openDrawer,
            )
        }
    }
}