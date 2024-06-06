package com.pablofraile.montcoin.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.SupervisedUserCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pablofraile.montcoin.model.Amount
import com.pablofraile.montcoin.model.Id
import com.pablofraile.montcoin.model.User

data class UserSelectable(val user: User, val isSelected: Boolean)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserSelectable(
    users: List<UserSelectable>,
    onUserClicked: (User) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 2.dp, start = 2.dp)
            .fillMaxHeight()
    ) {
        items(users, key = { it.user.id.value }) { element ->
            Box(modifier = Modifier.animateItemPlacement()) {
                UserSelectableItem(
                    user = element.user,
                    isSelected = element.isSelected,
                    onUserClicked = onUserClicked
                )
            }
        }
    }
}

@Composable
fun UserSelectableItem(
    user: User,
    isSelected: Boolean,
    onUserClicked: (User) -> Unit,
) {
    val color =
        if (isSelected) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surface
    Surface(
        onClick = { onUserClicked(user) },
        color = color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                imageVector = Icons.Sharp.SupervisedUserCircle,
                contentDescription = null,
                modifier = Modifier.size(58.dp)
            )
            Column {
                Text(text = user.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$ ${user.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UsersScreenPreview() {
    UserSelectable(
        users = listOf(
            UserSelectable(User(Id("1"), "Pablo Fraile", Amount(90)), false),
            UserSelectable(User(Id("2"), "John Doe", Amount(100)), true),
            UserSelectable(User(Id("3"), "Jane Doe", Amount(-100)), false),
        ),
        onUserClicked = {}
    )
}