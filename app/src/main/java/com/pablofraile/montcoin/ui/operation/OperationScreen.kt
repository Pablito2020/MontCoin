package com.pablofraile.montcoin.ui.operation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.ui.LoadingAnimation
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun OperationScreen(
    amount: String,
    amountIsValid: Boolean,
    showOperationResult: Boolean,
    card: NfcSensor,
    operation: OperationResult?,
    isDoingOperation: Boolean,
    onOperationErrorReaded: () -> Unit,
    onOperationCorrectReaded: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onAmountChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShowOperationUi(isDoingOperation=isDoingOperation, showOperationResult=showOperationResult, operation = operation, onOperationErrorReaded = onOperationErrorReaded, onOperationCorrectReaded= onOperationCorrectReaded)
        AmountTextBox(
            amount = amount,
            isValid = amountIsValid,
            onAmountChange = onAmountChange,
            onDone = onStart
        )
        Spacer(modifier = Modifier.height(16.dp))
        ActionButton(cardState = card, onStart = onStart, onStop = onStop)
        if (card == NfcSensor.Searching) {
            Spacer(modifier = Modifier.height(16.dp))
            LoadingAnimation()
        }
    }
}

@Composable
fun ShowOperationUi(
    isDoingOperation: Boolean,
    showOperationResult: Boolean,
    operation: OperationResult?,
    onOperationErrorReaded: () -> Unit,
    onOperationCorrectReaded: () -> Unit,
) {
    if (isDoingOperation) ShowOperationDoingDialog()
    if (!showOperationResult || operation == null) return
    when (operation) {
        is OperationResult.Error -> ErrorOperationDialog(
            operation = operation,
            onOperationErrorReaded = onOperationErrorReaded
        )
        OperationResult.Success -> ShowSnackBar(
            text = "Operation Done correctly!",
            onClosedSnackBar = onOperationCorrectReaded
        )
    }
}

@Composable
fun ShowSnackBar(text: String, onClosedSnackBar: () -> Unit = {}) {
    onClosedSnackBar()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = snackbarHostState) {
        scope.launch {
            snackbarHostState.showSnackbar(text)
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
    operation: OperationResult.Error,
    onOperationErrorReaded: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = @Composable {
            TextButton(onClick = onOperationErrorReaded) {
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
            Text("Error Message: ${operation.message}")
        })
}

@OptIn(ExperimentalComposeUiApi::class)
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
        onValueChange = onAmountChange,
        keyboardOptions = keyboardConfig,
        keyboardActions = keyboardActions,
        leadingIcon = {
            Icon(Icons.Filled.AttachMoney, contentDescription = "Amount of Money")
        })
}

@Composable
fun ActionButton(cardState: NfcSensor, onStart: () -> Unit, onStop: () -> Unit) {
    Button(
        onClick = {
            when (cardState) {
                is NfcSensor.Stopped -> onStart()
                is NfcSensor.Searching -> onStop()
            }
        }
    ) {
        Text(
            text = when (cardState) {
                is NfcSensor.Stopped -> "Start"
                is NfcSensor.Searching -> "Stop"
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun OperationScreenPreview() {
    OperationScreen(
        amount = "100",
        amountIsValid = true,
        operation = OperationResult.Success,
        card = NfcSensor.Searching,
        onOperationErrorReaded = {},
        onStart = {},
        onStop = {},
        onAmountChange = {},
        isDoingOperation = false,
        showOperationResult = false,
        onOperationCorrectReaded = {},
    )
}