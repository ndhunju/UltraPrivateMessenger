package com.ndhunju.ultraprivatemessenger.android.ui.threads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ndhunju.ultraprivatemessenger.android.R
import com.ndhunju.ultraprivatemessenger.android.ui.common.CenteredMessageWithButton
import com.ndhunju.ultraprivatemessenger.android.ui.common.NavAppBar
import com.ndhunju.ultraprivatemessenger.android.ui.common.ScrollToTopLaunchedEffect
import com.ndhunju.ultraprivatemessenger.android.ui.common.asSnapshotStateList
import com.ndhunju.ultraprivatemessenger.android.ui.navigation.AppNavigationDrawer
import com.ndhunju.ultraprivatemessenger.data.sampleMessages
import com.ndhunju.ultraprivatemessenger.ui.threads.Message
import com.ndhunju.ultraprivatemessenger.ui.threads.ThreadsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Preview
@Composable
fun ThreadScreenPreview() {
    val dummyMessages = remember { mutableStateListOf<Message>().apply { addAll(sampleMessages) } }
    ThreadListContent(
        lastMessageList = dummyMessages,
        showErrorMessageForPermissionDenied = MutableStateFlow(false).collectAsState()
    )
}

@Composable
fun ThreadListContentWithNavDrawer(
    viewModel: ThreadsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var onClickLauncherIconCount = remember { 0 }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    AppNavigationDrawer(
        drawerState = drawerState,
        onClickNavItem = viewModel.onClickNavItem,
        onClickLauncherIcon = {
            onClickLauncherIconCount++
            if (onClickLauncherIconCount > 3) {
                onClickLauncherIconCount = 0
                viewModel.doOpenDebugScreen?.invoke()
                coroutineScope.launch { drawerState.close() }
            }
        }
    ) {
        ThreadListContent(
            viewModel.title.collectAsState(""),
            viewModel.isRefresh.collectAsState(),
            viewModel.showProgress.collectAsState(),
            viewModel.showUpIcon.collectAsState(false),
            viewModel.showSearchTextField.collectAsState(),
            viewModel.showErrorMessageForPermissionDenied.collectAsState(),
            viewModel.lastMessageForEachThread.asSnapshotStateList(),
            viewModel.onRefreshByUser,
            viewModel.onClickSearchIcon,
            viewModel.onSearchTextChanged,
            viewModel.onClickGrantPermission,
            viewModel.onClickThreadMessage,
            onClickMenuOrUpIcon = {
                coroutineScope.launch {drawerState.open() }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThreadListContent(
    title: State<String>? = null,
    isRefreshing: State<Boolean>? = null,
    showProgress: State<Boolean>? = null,
    showUpIcon: State<Boolean>? = mutableStateOf(true),
    showSearchTextField: State<Boolean>? = null,
    showErrorMessageForPermissionDenied: State<Boolean>? = null,
    lastMessageList: SnapshotStateList<Message>,
    onRefreshByUser: (() -> Unit)? = null,
    onClickSearchIcon: (() -> Unit)? = null,
    onSearchTextChanged: ((String) -> Unit)? = null,
    onClickGrantPermission: (() -> Unit)? = null,
    onClickMessage: ((Message) -> Unit)? = null,
    onClickMenuOrUpIcon: (() -> Unit)? = null
) {
    val composeCoroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    // Use derivedStateOf to avoid recomposition everytime state changes.
    // That is everytime [state.firstVisibleItemIndex] changes instead
    // of the specific condition we are interested in.
    val showScrollToTopButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    ScrollToTopLaunchedEffect(lastMessageList, listState)

    Scaffold(
        topBar = {
            NavAppBar(
                title,
                showUpIcon,
                showSearchTextField,
                // Put all callbacks inside lambda so that recomposition
                // is not triggered when reference to those callback changes?
                { onClickSearchIcon?.invoke() },
                { onSearchTextChanged?.invoke(it) },
                onClickMenuOrUpIcon
            )
        },
        floatingActionButton = {
            if (showScrollToTopButton) {
                FloatingActionButton(onClick = {
                    composeCoroutineScope.launch {
                        listState.animateScrollToItem(0, 0)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(
                            R.string.image_description_scroll_to_top
                        )
                    )
                }
            }
        }

    ) { innerPadding ->
        AnimatedVisibility(
            visible = showErrorMessageForPermissionDenied?.value == true,
            exit = fadeOut()
        ) {
            CenteredMessageWithButton(
                modifier = Modifier.padding(innerPadding),
                message = stringResource(id = R.string.permission_rationale),
                buttonText = stringResource(R.string.grant_permissions),
                onClickButton = onClickGrantPermission
            )
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing?.value ?: false,
            onRefresh = { onRefreshByUser?.invoke() }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            AnimatedVisibility(
                visible = !(showErrorMessageForPermissionDenied?.value ?: false),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ThreadList(
                    Modifier.padding(innerPadding),
                    listState,
                    lastMessageList,
                    onClickMessage
                )
            }

            if (showProgress?.value == true) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }

            PullRefreshIndicator(
                isRefreshing?.value ?: false,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
private fun ThreadList(
    modifier: Modifier,
    lazyListState: LazyListState,
    lastMessageList: SnapshotStateList<Message>?,
    onClickMessage: ((Message) -> Unit)?
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("threadList")
            // Add this to use threadList as id for UiAutomation
            // Search for "threadList" to find where
            .semantics { testTagsAsResourceId = true },
        state = lazyListState,
        content = {
            itemsIndexed(
                items = lastMessageList?.toList() ?: emptyList(),
                // Pass key for better performance like setHasStableIds
                key = { _, item -> item.threadId },
            ) { _: Int, message: Message ->
                ThreadListItem(
                    Modifier.animateItemPlacement(tween(durationMillis = 250)),
                    message,
                    onClickMessage
                )
            }
        }
    )
}