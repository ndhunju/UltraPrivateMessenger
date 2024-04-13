package com.ndhunju.ultraprivatemessenger.android.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<List<T>>.asSnapshotStateList(): SnapshotStateList<T> {
    val snapshotStateList = remember { mutableStateListOf<T>() }
    LaunchedEffect(key1 = "MutableStateListFlow.asSnapshotStateList") {
        snapshotStateList.addAll(value)
        collect {
            snapshotStateList.clear()
            snapshotStateList.addAll(it)
        }
    }
    return snapshotStateList
}