package com.ndhunju.ultraprivatemessenger.android.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.ndhunju.ultraprivatemessenger.common.MutableStateListFlow.Operation
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<Operation<T>>.asSnapshotStateList(): SnapshotStateList<T> {
    val snapshotStateList = remember { mutableStateListOf<T>() }
    LaunchedEffect(key1 = "MutableStateListFlow.asSnapshotStateList") {
        //snapshotStateList.addAll(value)
        collect {
            when (value) {
                is Operation.Add<T> -> {
                    val addOp = value as Operation.Add
                    snapshotStateList.add(addOp.index, addOp.item)
                }
                is Operation.AddAll -> {
                    val addAllOp = value as Operation.AddAll
                    snapshotStateList.addAll(addAllOp.items)
                }
                is Operation.Clear<T> -> {
                    snapshotStateList.clear()
                }
                is Operation.DataSetChanged<T>  -> {
                    val dataSetChanged = value as Operation.DataSetChanged
                    snapshotStateList.clear()
                    snapshotStateList.addAll(dataSetChanged.items)
                }
                is Operation.None<T> -> {}
                is Operation.RemoveAt<T> -> {
                    val removeAt = value as Operation.RemoveAt
                    snapshotStateList.removeAt(removeAt.index)
                }
                is Operation.Set<T> -> {
                    val set = value as Operation.Set
                    snapshotStateList[set.index] = set.item
                }
            }
        }
    }
    return snapshotStateList
}