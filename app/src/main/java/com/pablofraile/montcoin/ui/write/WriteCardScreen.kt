package com.pablofraile.montcoin.ui.write

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.Autocomplete
import com.pablofraile.montcoin.ui.common.LoadingAnimation
import com.pablofraile.montcoin.ui.common.Sensor
import com.pablofraile.montcoin.ui.common.UserChip
import com.pablofraile.montcoin.ui.operation.ActionButton
import com.pablofraile.montcoin.ui.operation.ErrorOperationDialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteCardScreen(
    sensor: Sensor,
    writeResult: Pair<User, Date>?,
    users: List<User>,
    selectedUser: User?,
    onSelectedUser: (User) -> Unit,
    errorMessage: String?,
    onOkError: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onClearUser: () -> Unit,
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
                        text = "Write Card",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.GraphicEq, contentDescription = null)
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
        WriteCardContent(
            sensor = sensor,
            writeResult = writeResult,
            users = users,
            onSelectedUser = onSelectedUser,
            selectedUser = selectedUser,
            onStart = onStart,
            onStop = onStop,
            errorMessage = errorMessage,
            onOkError = onOkError,
            onClearUser = onClearUser,
            snackbarHostState = snackbarHostState,
            modifier = modifier
        )
    }
}


@Composable
fun ShowSnackBar(
    user: Pair<User, Date>,
    snackbarHostState: SnackbarHostState,
    onClosedSnackBar: () -> Unit = {}
) {
    LaunchedEffect(key1 = user.second) {
        launch {
            snackbarHostState.showSnackbar("Written card for ${user.first.name}")
            onClosedSnackBar()
        }
    }
}


@Composable
fun WriteCardContent(
    sensor: Sensor,
    writeResult: Pair<User, Date>?,
    errorMessage: String?,
    users: List<User>,
    selectedUser: User?,
    onSelectedUser: (User) -> Unit,
    onClearUser: () -> Unit,
    onOkError: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    if (selectedUser == null ) {
        Autocomplete(
            label = "Search User",
            items = users,
            modifier = modifier,
            onItemSelected = onSelectedUser,
            toString = { it.name })
        Spacer(modifier = modifier.height(16.dp))
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            UserChip(userName = selectedUser.name, onClose = onClearUser)
            ActionButton(cardState = sensor, onStart = onStart, onStop = onStop)
        }
        if (sensor == Sensor.Searching) {
            Spacer(modifier = Modifier.height(16.dp))
            LoadingAnimation()
        }
        if (writeResult != null)
            ShowSnackBar(
                user = writeResult,
                snackbarHostState = snackbarHostState
            )
        if (errorMessage != null) {
            ErrorOperationDialog(message = errorMessage, onOk = onOkError)
        }
    }
}