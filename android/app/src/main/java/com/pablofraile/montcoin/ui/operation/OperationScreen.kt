package com.pablofraile.montcoin.ui.operation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.ui.common.Chart
import com.pablofraile.montcoin.ui.common.IncomeChartData
import com.pablofraile.montcoin.ui.common.SearchingAnimation
import com.pablofraile.montcoin.ui.common.Sensor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m2.common.rememberM2VicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.launch
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationScreen(
    amount: String,
    amountIsValid: Boolean,
    card: Sensor,
    operation: Operation?,
    errorMessage: String?,
    modelProducer: CartesianChartModelProducer = remember { CartesianChartModelProducer.build() },
    closeError: () -> Unit,
    onRefresh: () -> Unit = {},
    isDoingOperation: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onFailOperation: (Boolean) -> Unit,
    enabledFailed: Boolean,
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
                            onRefresh()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
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
            modelProducer = modelProducer,
            isDoingOperation = isDoingOperation,
            errorMessage = errorMessage,
            closeError = closeError,
            onStart = onStart,
            onStop = onStop,
            onAmountChange = onAmountChange,
            modifier = Modifier.padding(innerPadding),
            onFailOperation = onFailOperation,
            enabledFailed = enabledFailed,
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
    modelProducer: CartesianChartModelProducer = remember { CartesianChartModelProducer.build() },
    errorMessage: String?,
    closeError: () -> Unit,
    isDoingOperation: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onAmountChange: (String) -> Unit,
    onFailOperation: (Boolean) -> Unit,
    enabledFailed: Boolean,
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
        Chart(modelProducer)
        HorizontalDivider(modifier=Modifier.padding(30.dp))
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
        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Switch(
                thumbContent = {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null
                    )
                },
                checked = enabledFailed,
                onCheckedChange = onFailOperation
            )
            ActionButton(cardState = card, onStart = onStart, onStop = onStop)
        }
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
    onDone: () -> Unit = {}
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
        onFailOperation = {},
        enabledFailed = true,
        closeError = {},
    )
}