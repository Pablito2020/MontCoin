package com.pablofraile.montcoin.ui.routes

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.ui.theme.MontCoinTheme

@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToOperation: () -> Unit,
    navigateToTransactions: () -> Unit,
    navigateToWriteCard: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        MontCoinLogo(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
        )
        NavigationDrawerItem(
            label = { Text(text = "Maneja") },
            icon = { Icon(Icons.Filled.Euro, null) },
            selected = currentRoute == MontCoinDestinations.OPERATION_ROUTE,
            onClick = { navigateToOperation(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Operacions") },
            icon = { Icon(Icons.AutoMirrored.Filled.FeaturedPlayList, null) },
            selected = currentRoute == MontCoinDestinations.OPERATIONS_ROUTE,
            onClick = { navigateToTransactions(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        // Add Blanck component to separate the last item
        NavigationDrawerItem(
            label = { Text("Tarjeta") },
            icon = { Icon(Icons.Filled.CreditCard, null) },
            selected = false,
            onClick = { navigateToWriteCard(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun MontCoinLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "MontCoin",
        )
        Text(
            text = "MontCoin",
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    MontCoinTheme {
        AppDrawer(
            currentRoute = MontCoinDestinations.OPERATION_ROUTE,
            navigateToOperation = {},
            navigateToTransactions = {},
            navigateToWriteCard = {},
            closeDrawer = {},
        )
    }
}