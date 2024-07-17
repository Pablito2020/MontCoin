package net.pablofraile

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import net.pablofraile.ui.theme.PosterminalTheme


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val _viewModel: MainViewModel = viewModel(
                factory = MainViewModel.provideFactory(
                )
            )
            val searching by _viewModel.search.collectAsStateWithLifecycle()
            PosterminalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(searching = searching, onTimeout={ _viewModel.setSearching(true)}, modifier=Modifier.padding(innerPadding))
                }
            }
        }
    }

}

@Composable
fun MainScreen(searching: Boolean, onTimeout: () -> Unit, modifier: Modifier = Modifier) {
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastInteractionTime) {
        while (true) {
            delay(1000) // Check every second
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastInteractionTime > 4000) {
                onTimeout()
                lastInteractionTime = currentTime // Reset to avoid repeated calls
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    lastInteractionTime = System.currentTimeMillis()
                }
            }
    ) {
        if (searching) {
            Greeting("Search")
        }else {
            Greeting("Not search")
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PosterminalTheme {
        Greeting("Android")
    }
}