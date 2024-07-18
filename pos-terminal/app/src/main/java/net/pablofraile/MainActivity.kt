package net.pablofraile

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import net.pablofraile.data.card.nfc.NfcCardRepository
import net.pablofraile.model.Operation
import net.pablofraile.model.User
import net.pablofraile.ui.common.SearchingAnimation
import net.pablofraile.ui.common.Sensor
import net.pablofraile.ui.theme.PosterminalTheme
import net.pablofraile.ui.user.Percentage
import net.pablofraile.ui.user.UserScreen
import net.pablofraile.utils.NfcActivityTemplate


class MainActivity : NfcActivityTemplate() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.provideFactory(
                    (application as PosApplication).container.cardRepository,
                    (application as PosApplication).container.operationsRepository,
                    (application as PosApplication).container.usersRepository
                )
            )
            val userId by viewModel.userId.collectAsStateWithLifecycle(null)
            val user by viewModel.user.collectAsStateWithLifecycle()
            val operations by viewModel.operations.collectAsStateWithLifecycle()
            val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
            val percentage by viewModel.percentage.collectAsStateWithLifecycle()
            val lastInteraction by viewModel.lastInteraction.collectAsStateWithLifecycle()
            val searching by viewModel.sensor.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            PosterminalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        searching = searching,
                        userId = userId,
                        onTimeout = { viewModel.searchDevices() },
                        user=user,
                        lastInteraction = lastInteraction,
                        operations=operations,
                        errorMessage=errorMessage,
                        percentage=percentage,
                        isLoading = isLoading,
                        onClick = viewModel::updateLastInteraction,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override suspend fun onTagRead(intent: Intent?) {
        val nfc = ((application as PosApplication).container.cardRepository as NfcCardRepository)
        nfc.send(intent)
    }

}

@Composable
fun MainScreen(
    searching: Sensor,
    lastInteraction: Long,
    userId: String?,
    user: User?,
    onClick: () -> Unit,
    operations: List<Operation>,
    errorMessage: String?,
    percentage: Percentage?,
    onTimeout: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(lastInteraction) {
        while (true) {
            delay(1000) // Check every second
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastInteraction > 6000) {
                onTimeout()
                onClick()
            }
        }
    }
    Box(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures {
                onClick()
            }
            detectTransformGestures { _, _ ,_ ,_ ->
                onClick()
            }
            detectHorizontalDragGestures { change, dragAmount ->  onClick()}
            detectVerticalDragGestures { change, dragAmount ->  onClick()}
            detectDragGestures { a, b -> onClick() }
        }) {
        if (searching == Sensor.Searching) {
            Spacer(modifier = Modifier.height(16.dp))
            SearchingAnimation()
        } else if (searching == Sensor.Stopped && userId != null && user != null) {
            UserScreen(
                isLoading = isLoading,
                user = user,
                operations = operations,
                errorMessage = errorMessage,
                percentage = percentage
            )
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PosterminalTheme {
        Greeting("Android")
    }
}