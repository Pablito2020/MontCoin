package com.pablofraile.montcoin.ui.users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    onClick: (User) -> Unit,
    onSearchChange: (String) -> Unit,
    openDrawer: () -> Unit,
    onChangeOrder: (Order) -> Unit,
    onRefresh: suspend () -> Unit,
    fetchUsersFirstTime: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
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
                    if (users.isEmpty()) return@CenterAlignedTopAppBar
                    ListOrderDropDown(
                        currentOrder = currentOrder,
                        onChangeOrder = onChangeOrder,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        UsersContents(
            users = users,
            onRefresh = onRefresh,
            isLoading = isLoading,
            errorMessage = errorMessage,
            searchValue = search,
            onClick = onClick,
            onSearchChange = onSearchChange,
            fetchUsersFirstTime = fetchUsersFirstTime,
            snackbarHostState = snackbarHostState,
            modifier = modifier
        )
    }
}

@Composable
fun UsersContents(
    modifier: Modifier = Modifier,
    users: List<User> = emptyList(),
    errorMessage: String? = null,
    isLoading: Boolean = false,
    searchValue: String = "",
    onClick: (User) -> Unit = {},
    onRefresh: suspend () -> Unit = {},
    fetchUsersFirstTime: () -> Unit = {},
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
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchValue,
                onValueChange = onSearchChange,
                label = { Text("Search User") },
                keyboardOptions = keyboardConfig,
                keyboardActions = keyboardActions,
                modifier = Modifier.weight(6f),
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search User")
                })
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
            if (errorMessage != null) {
                RefreshMessage(
                    message = errorMessage,
                    buttonMessage = "Try Again",
                    onRefresh = fetchUsersFirstTime,
                    buttonColor = MaterialTheme.colorScheme.errorContainer
                )
            } else {
                ListUsers(users, onClick, fetchUsersFirstTime, onRefresh, snackbarHostState)
            }
        }
    }
}

@Composable
fun RefreshMessage(
    message: String,
    buttonMessage: String,
    onRefresh: () -> Unit,
    buttonColor: Color = MaterialTheme.colorScheme.errorContainer,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = message
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
                    .clickable { onRefresh() },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(
                            ButtonDefaults.shape
                        )
                        .background(buttonColor)
                ) {
                    Text(buttonMessage, modifier = Modifier.padding(10.dp))
                    Icon(
                        imageVector = Icons.Filled.Replay,
                        contentDescription = "reload",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ListUsers(
    users: List<User>,
    onClick: (User) -> Unit,
    fetchUsersFirstTime: () -> Unit,
    onRefresh: suspend () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold { inner ->
        if (users.isEmpty()) {
            RefreshMessage(
                message = "No users found!",
                buttonMessage = "Refresh Page",
                onRefresh = fetchUsersFirstTime,
                buttonColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
        InfiniteScroll(
            elements = users,
            itemRender = @Composable { user, m ->
                UserItem(user = user, onClick = onClick)
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
        modifier,
    ) {
        Menu(
            currentElement = currentOrder, elements = listOf(
                MenuAction("Name", Order.UserName),
                MenuAction("Amount Ascendant", Order.AmountAscendant),
                MenuAction("Amount Descendant", Order.AmountDescendant),
                MenuAction("Operations Ascendant", Order.NumberOperationsAscendant),
                MenuAction("Operations Descendant", Order.NumberOperationsDescendant)
            ), onElementSelected = onChangeOrder
        )
    }
}

@Composable
fun UserItem(user: User, onClick: (User) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = { onClick(user) }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.SupervisedUserCircle,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                val typography = when (user.name.length) {
                    in 0..20 -> MaterialTheme.typography.headlineMedium
                    in 21..30 -> MaterialTheme.typography.headlineSmall
                    in 30..40 -> MaterialTheme.typography.bodyLarge
                    in 40..50 -> MaterialTheme.typography.bodyMedium
                    else -> MaterialTheme.typography.bodySmall
                }
                BasicText(
                    text = user.name,
                    style = TextStyle(
                        fontSize = typography.fontSize,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicText(
                        text = "${user.amount.value} \uD83E\uDE99",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                        )
                    )
                    BasicText(
                        text = "${user.numberOfOperations} \uD83D\uDCB3",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                        )
                    )
                }
            }

        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun UsersScreenPreview() {
    UsersContents(
        users = emptyList(),
        errorMessage = null,
        isLoading = false
    )
}