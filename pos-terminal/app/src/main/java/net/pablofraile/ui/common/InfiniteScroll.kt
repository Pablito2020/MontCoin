package net.pablofraile.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun <T> InfiniteScroll(
    elements: List<T>,
    itemRender: @Composable (T, Modifier) -> Unit,
    onRefresh: suspend () -> Unit,
    loadMoreItems: () -> Unit,
    refreshedMessage: String,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    cachingKey: ((item: T) -> Any)? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
            val showSnackBar =
                coroutineScope.launch { snackbarHostState.showSnackbar(refreshedMessage) }
            state.endRefresh()
            showSnackBar.join()
        }
    }
    Box(modifier = modifier.nestedScroll(state.nestedScrollConnection)) {
        LazyColumn(modifier = Modifier.padding(top=2.dp, start=2.dp).fillMaxHeight()) {
            items(elements, key=cachingKey) { element ->
                Box(modifier = Modifier.animateItemPlacement()) {
                    itemRender(element, modifier)
                }
                val index = elements.indexOf(element)
                if (index == elements.size - 1)
                    loadMoreItems()
            }
        }
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = state,
        )
    }
}
