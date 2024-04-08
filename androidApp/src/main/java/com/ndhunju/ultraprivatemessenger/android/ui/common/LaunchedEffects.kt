package com.ndhunju.ultraprivatemessenger.android.ui.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

@Composable
fun ScrollToTopLaunchedEffect(key: Any?, state: LazyListState) {
    LaunchedEffect(key1 = key) {
        snapshotFlow { state.firstVisibleItemIndex == 0 }
            .collect { state.animateScrollToItem(0) }
    }
}