package com.pablofraile.montcoin.ui.users

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.InfiniteScroll
import com.pablofraile.montcoin.ui.common.Menu
import com.pablofraile.montcoin.ui.common.MenuAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    users: List<User>,
    isLoading: Boolean,
    currentOrder: Order,
    errorMessage: String?,
    search: String,
    onSearchChange: (String) -> Unit,
    openDrawer: () -> Unit,
    onChangeOrder: (Order) -> Unit,
    onRefresh: suspend () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Usuaris",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Search is not yet implemented in this configuration",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "search"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        UsersContents(
            users = users,
            onRefresh = onRefresh,
            isLoading = isLoading,
            searchValue = search,
            onSearchChange = onSearchChange,
            currentOrder = currentOrder,
            onChangeOrder = onChangeOrder,
            errorMessage = errorMessage,
            snackbarHostState = snackbarHostState,
            modifier = modifier
        )
    }
}

@Composable
fun UsersContents(
    currentOrder: Order,
    modifier: Modifier = Modifier,
    users: List<User> = emptyList(),
    isLoading: Boolean = false,
    searchValue: String = "",
    errorMessage: String? = null,
    onChangeOrder: (Order) -> Unit = { _ -> },
    onRefresh: suspend () -> Unit = {},
    onSearchChange: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val keyboardConfig = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Go
        )
        val keyboardController = LocalSoftwareKeyboardController.current
        val keyboardActions = KeyboardActions(onGo = { keyboardController?.hide() })
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchValue,
                onValueChange = onSearchChange,
                keyboardOptions = keyboardConfig,
                keyboardActions = keyboardActions,
                modifier = Modifier.weight(6f),
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search User")
                })
            ListOrderDropDown(
                currentOrder = currentOrder,
                onChangeOrder = onChangeOrder,
                modifier = Modifier.weight(1f)
            )
        }
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            ListUsers(users, onRefresh, snackbarHostState)
        }
    }
}

@Composable
private fun ListUsers(
    users: List<User>,
    onRefresh: suspend () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold { inner ->
        InfiniteScroll(
            elements = users,
            itemRender = @Composable { user, m ->
                Text(text = user.name, modifier = m)
            },
            onRefresh = onRefresh,
            loadMoreItems = { },
            refreshedMessage = "Users refreshed!",
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        )
    }
}

@Composable
fun RowScope.ListOrderDropDown(
    currentOrder: Order,
    modifier: Modifier = Modifier,
    onChangeOrder: (Order) -> Unit = { _ -> },
) {
    Box(
        modifier
            .fillMaxWidth()
            .align(Alignment.CenterVertically),
    ) {
        Menu(
            currentElement = currentOrder, elements = listOf(
                MenuAction("Name", Order.UserName),
                MenuAction("Amount", Order.Amount),
                MenuAction("Operations (Asc)", Order.NumberOperationsAscendant),
                MenuAction("Operations (Desc)", Order.NumberOperationsDescendant)
            ), onElementSelected = onChangeOrder
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun UsersScreenPreview() {
    UsersContents(isLoading = true, currentOrder = Order.UserName)
}