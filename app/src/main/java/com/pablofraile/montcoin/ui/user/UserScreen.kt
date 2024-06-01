package com.pablofraile.montcoin.ui.user

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.AnimatedCircle
import java.time.Instant
import java.util.Date
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    isLoadingUser: Boolean,
    isLoadingOperations: Boolean,
    user: User?,
    operations: List<Operation>,
    goBack: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = user?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (isLoadingOperations) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(64.dp)
                        ) {
                            val state = PullToRefreshState(
                                positionalThresholdPx = 0.toFloat(),
                                initialRefreshing = true,
                                enabled = { true }
                            )
                            PullToRefreshDefaults.Indicator(
                                state = state,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Refresh is not yet implemented in this configuration",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "refresh"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        if (isLoadingUser) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            Column(modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()) {
                Box(Modifier.padding(16.dp)) {
                    val accountsProportion = listOf(0.40f, 0.60f)
                    val circleColors = listOf(
                        Color(0xFF73FF94),
                        Color(0xFFFF7777),
                    )
                    AnimatedCircle(
                        accountsProportion,
                        circleColors,
                        Modifier
                            .height(300.dp)
                            .align(Alignment.Center)
                            .fillMaxWidth()
                    )
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text(
                            text = "Amount",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "${user!!.amount} \uD83D\uDCB8",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                HorizontalDivider()
                if (isLoadingOperations) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    if (operations.isEmpty()) {
                        Text(
                            text = "No operations found \uD83E\uDEF0",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                            modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Card(modifier = Modifier.padding(12.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                operations.forEach { item ->
                                    Text(
                                        text = item.amount.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth()
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    val user = User(
        id = Id("1"),
        name = "Pablo Fraile",
        amount = Amount(1000),
        numberOfOperations = 10
    )
    UserScreen(
        user = User(
            id = Id("1"),
            name = "Pablo Fraile",
            amount = Amount(1000),
            numberOfOperations = 10
        ),
        isLoadingUser = false,
        isLoadingOperations = false,
        operations = emptyList(),
//        operations = (0..10).map {
//            Operation(
//                id = UUID.randomUUID(),
//                amount = Amount(it * 100),
//                date = Date.from(Instant.now()),
//                user = user
//            )
//        }
    )
}
