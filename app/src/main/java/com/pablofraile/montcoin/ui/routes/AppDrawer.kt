package com.pablofraile.montcoin.ui.routes

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    navigateToListUsers: () -> Unit,
    navigateToBulkOperation: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        MontCoinLogo(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
        Text(
            text = "Cards",
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 24.dp),
            fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
            color = MaterialTheme.colorScheme.onSurface
        )
        NavigationDrawerItem(
            label = { Text(text = "Pay") },
            icon = { Icon(Icons.Filled.CreditCard, null) },
            selected = currentRoute == MontCoinDestinations.OPERATION_ROUTE,
            onClick = { navigateToOperation(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Configure") },
            icon = { Icon(Icons.Filled.Nfc, null) },
            selected = currentRoute == MontCoinDestinations.WRITE_CARD,
            onClick = { navigateToWriteCard(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
        Text(
            text = "Data",
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 24.dp),
            fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
            color = MaterialTheme.colorScheme.onSurface
        )
        NavigationDrawerItem(
            label = { Text("Users") },
            icon = { Icon(Icons.Filled.People, null) },
            selected = currentRoute == MontCoinDestinations.LIST_USERS,
            onClick = { navigateToListUsers(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Operacions") },
            icon = { Icon(Icons.AutoMirrored.Filled.FeaturedPlayList, null) },
            selected = currentRoute == MontCoinDestinations.OPERATIONS_ROUTE,
            onClick = { navigateToTransactions(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

        Text(
            text = "Bulk",
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 24.dp),
            fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
            color = MaterialTheme.colorScheme.onSurface
        )
        NavigationDrawerItem(
            label = { Text("Operation") },
            icon = { Icon(Icons.Filled.Euro, null) },
            selected = currentRoute == MontCoinDestinations.BULK_OPERATION,
            onClick = { navigateToBulkOperation(); closeDrawer() },
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
            navigateToListUsers = {},
            navigateToBulkOperation = {},
            closeDrawer = {},
        )
    }
}