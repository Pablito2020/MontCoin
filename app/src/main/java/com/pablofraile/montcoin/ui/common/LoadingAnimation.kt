package com.pablofraile.montcoin.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LoadingAnimation() {
    val state = PullToRefreshState(
        positionalThresholdPx = 0.toFloat(),
        initialRefreshing = true,
        enabled = { true }
    )
    PullToRefreshDefaults.Indicator(state = state)
}