package com.pablofraile.montcoin.ui.operations

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pablofraile.montcoin.model.Operation
import com.pablofraile.montcoin.model.Operations
import com.pablofraile.montcoin.ui.common.InfiniteScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsScreen(
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    operations: Operations,
    loadMoreItems: () -> Unit,
    onRefresh: suspend () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Operations",
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
        InfiniteScroll(
            elements = operations,
            itemRender = { operation, modifier ->
                OperationItem(
                    operation = operation,
                    modifier = modifier
                )
            },
            onRefresh = onRefresh,
            loadMoreItems = loadMoreItems,
            refreshedMessage = "Loaded last Operations!",
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun OperationItem(operation: Operation, modifier: Modifier = Modifier) {
    val amountValue = operation.amount.toInt()
    val amountColor = if (amountValue >= 0) Color.Green else Color.Red
    Card(modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)) {
        Row(
            modifier = Modifier
                .padding(start = 32.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 0,
                        easing = LinearEasing,
                    ),
                )
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "User: ${operation.user.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Date: ${operation.date}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = operation.amount.value,
                fontSize = 20.sp,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}