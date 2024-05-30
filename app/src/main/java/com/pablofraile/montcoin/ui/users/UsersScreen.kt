package com.pablofraile.montcoin.ui.users

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.InfiniteScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    users: List<User>,
    isLoading: Boolean,
    onRefresh: suspend () -> Unit,
    errorMessage: String?,
    openDrawer: () -> Unit,
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
        if (isLoading) {
            val state = PullToRefreshState(
                positionalThresholdPx = 0.toFloat(),
                initialRefreshing = true,
                enabled = { true }
            )
            PullToRefreshDefaults.Indicator(state = state)
        } else {
            InfiniteScroll(
                elements = users,
                itemRender = @Composable { user, m ->
                    Text(text = user.name, modifier=m)
                },
                onRefresh = onRefresh,
                loadMoreItems = { },
                refreshedMessage = "Users refreshed!",
                snackbarHostState = snackbarHostState,
                modifier = modifier
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun UsersScreenPreview() {
}