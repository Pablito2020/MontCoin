package com.pablofraile.montcoin.ui.users

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.ui.Menu
import com.composables.ui.MenuButton
import com.composables.ui.MenuContent
import com.composables.ui.MenuItem
import com.composables.ui.rememberMenuState
import com.pablofraile.montcoin.model.User
import com.pablofraile.montcoin.ui.common.InfiniteScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    users: List<User>,
    isLoading: Boolean,
    onRefresh: suspend () -> Unit,
    errorMessage: String?,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Usuaris",
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
        val modifier = Modifier.padding(innerPadding)
        UsersContents(
            users = users,
            onRefresh = onRefresh,
            isLoading = isLoading,
            searchValue = "",
            onSearchChange = {},
            errorMessage = errorMessage,
            openDrawer = openDrawer,
            snackbarHostState = snackbarHostState,
            modifier = modifier
        )
    }
}

@Composable
fun UsersContents(
    users: List<User> = emptyList(),
    isLoading: Boolean = false,
    searchValue: String = "",
    errorMessage: String? = null,
    onRefresh: suspend () -> Unit = {},
    onSearchChange: (String) -> Unit = {},
    openDrawer: () -> Unit = {},
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val keyboardConfig = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Go
        )
        val keyboardController = LocalSoftwareKeyboardController.current
        val keyboardActions = KeyboardActions(onGo = { keyboardController?.hide() })
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchValue,
                onValueChange = onSearchChange,
                keyboardOptions = keyboardConfig,
                keyboardActions = keyboardActions,
                modifier = Modifier.weight(6f),
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search User")
                })
            SimpleDropdown(modifier = Modifier.weight(1f))
        }
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            ListUsers(users, onRefresh, snackbarHostState)
        }
    }
}

@Composable
private fun ListUsers(
    users: List<User>,
    onRefresh: suspend () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold { inner ->
        InfiniteScroll(
            elements = users,
            itemRender = @Composable { user, m ->
                Text(text = user.name, modifier = m)
            },
            onRefresh = onRefresh,
            loadMoreItems = { },
            refreshedMessage = "Users refreshed!",
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        )
    }
}

@Composable
fun RowScope.SimpleDropdown(modifier: Modifier = Modifier) {
    @Composable
    fun ChevronDown(
        color: Color
    ): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "ChevronDown",
                defaultWidth = 16.dp,
                defaultHeight = 16.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(color),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(6f, 9f)
                    lineToRelative(6f, 6f)
                    lineToRelative(6f, -6f)
                }
            }.build()
        }
    }
    Box(
        modifier
            .fillMaxWidth()
            .align(Alignment.CenterVertically),
    ) {
        Menu(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentWidth(),
            state = rememberMenuState(expanded = false)
        ) {
            MenuButton(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Image(ChevronDown(
                        MaterialTheme.colorScheme.onPrimaryContainer
                    ), null)
                }
            }

            MenuContent(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, MaterialTheme.colorScheme.surfaceTint, RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surface),
                hideTransition = fadeOut(),
            ) {

                val color = MaterialTheme.colorScheme.onSurface
                MenuItem(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)),
                    onClick = { /* TODO handle click */ }) {
                    BasicText(
                        "Name",
                        color={ color },
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(vertical = 10.dp, horizontal = 10.dp)
                    )
                }
            MenuItem(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)),
                    onClick = { /* TODO handle click */ }) {
                    BasicText(
                        "Money",
                        color = {color},
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(vertical = 10.dp, horizontal = 10.dp)
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun UsersScreenPreview() {
    UsersContents(isLoading = true)
}