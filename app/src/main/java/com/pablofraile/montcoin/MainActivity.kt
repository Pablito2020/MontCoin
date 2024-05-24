package com.pablofraile.montcoin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pablofraile.montcoin.ui.theme.MontCoinTheme
import com.pablofraile.montcoin.ui.transaction.CreditCardState
import com.pablofraile.montcoin.ui.transaction.TransactionScreen
import com.pablofraile.montcoin.ui.transaction.TransactionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MontCoinTheme {
                val model = viewModel(
                    factory = TransactionViewModel.provideFactory()
                ) as TransactionViewModel
                val uiState by model.uiState.collectAsStateWithLifecycle()
                TransactionScreen(
                    amount = "20",
                    card = CreditCardState.StoppedSearching,
                    onStart = {},
                    onStop = {},
                    onAmountChange = {})
            }
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
    MontCoinTheme {
        Greeting("Android")
    }
}