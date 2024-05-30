package com.pablofraile.montcoin.ui.operation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.ui.common.SearchingAnimation
import com.pablofraile.montcoin.ui.common.Sensor
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationScreen(
    amount: String,
    amountIsValid: Boolean,
    card: Sensor,
    operation: Operation?,
    errorMessage: String?,
    closeError: () -> Unit,
    isDoingOperation: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onAmountChange: (String) -> Unit,
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
                        text = "Opera",
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
        OperationContent(
            amount = amount,
            amountIsValid = amountIsValid,
            card = card,
            operation = operation,
            isDoingOperation = isDoingOperation,
            errorMessage = errorMessage,
            closeError = closeError,
            onStart = onStart,
            onStop = onStop,
            onAmountChange = onAmountChange,
            modifier = Modifier.padding(innerPadding),
            snackbarHostState = snackbarHostState
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun OperationContent(
    amount: String,
    amountIsValid: Boolean,
    card: Sensor,
    operation: Operation?,
    errorMessage: String?,
    closeError: () -> Unit,
    isDoingOperation: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ShowOperationUi(
            isDoingOperation = isDoingOperation,
            operation = operation,
            snackbarHostState = snackbarHostState,
            errorMessage = errorMessage,
            closeError = closeError
        )
        AmountTextBox(
            amount = amount,
            isValid = amountIsValid,
            onAmountChange = onAmountChange,
            onDone = onStart
        )
        Spacer(modifier = Modifier.height(16.dp))
        ActionButton(cardState = card, onStart = onStart, onStop = onStop)
        if (card == Sensor.Searching) {
            Spacer(modifier = Modifier.height(16.dp))
            SearchingAnimation()
        }
    }
}

@Composable
fun ShowOperationUi(
    isDoingOperation: Boolean,
    operation: Operation?,
    snackbarHostState: SnackbarHostState,
    errorMessage: String?,
    closeError: () -> Unit
) {
    if (isDoingOperation)
        ShowOperationDoingDialog()
    if (operation != null)
        ShowSnackBar(operation = operation, snackbarHostState = snackbarHostState)
    if (errorMessage != null)
        ErrorOperationDialog(message = errorMessage, onOk = closeError)
}

@Composable
fun ShowSnackBar(
    operation: Operation,
    snackbarHostState: SnackbarHostState,
    onClosedSnackBar: () -> Unit = {}
) {
    LaunchedEffect(key1 = operation) {
        launch {
            snackbarHostState.showSnackbar("Moved ${operation.amount.value} to ${operation.user.name}")
            onClosedSnackBar()
        }
    }
}

@Composable
fun ShowOperationDoingDialog(
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        title = {
            Text("Doing Operation...")
        },
        text = @Composable {
            CircularProgressIndicator()
        })
}

@Composable
fun ErrorOperationDialog(
    message: String,
    onOk: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = @Composable {
            TextButton(onClick = onOk) {
                Text("Ok")
            }
        },
        dismissButton = {},
        icon = @Composable {
            Icon(Icons.Filled.Warning, contentDescription = "Error")
        },
        title = @Composable {
            Text("Couldn't do operation!")
        },
        text = @Composable {
            Text("Error Message: $message")
        })
}

@Composable
fun AmountTextBox(
    amount: String,
    isValid: Boolean,
    onAmountChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val keyboardConfig = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Go
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardActions = KeyboardActions(onGo = {
        keyboardController?.hide()
        onDone()
    })
    OutlinedTextField(
        value = amount,
        isError = !isValid,
        supportingText = { if (!isValid) Text("Invalid Amount") },
        onValueChange = onAmountChange,
        keyboardOptions = keyboardConfig,
        keyboardActions = keyboardActions,
        leadingIcon = {
            Icon(Icons.Filled.AttachMoney, contentDescription = "Amount of Money")
        })
}

@Composable
fun ActionButton(cardState: Sensor, onStart: () -> Unit, onStop: () -> Unit) {
    Button(
        onClick = {
            when (cardState) {
                is Sensor.Stopped -> onStart()
                is Sensor.Searching -> onStop()
            }
        }
    ) {
        Text(
            text = when (cardState) {
                is Sensor.Stopped -> "Start"
                is Sensor.Searching -> "Stop"
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun OperationScreenPreview() {
    OperationContent(
        amount = "100",
        amountIsValid = true,
        operation = null,
        card = Sensor.Searching,
        onStart = {},
        onStop = {},
        onAmountChange = {},
        isDoingOperation = false,
        errorMessage = null,
        closeError = {},
    )
}