package com.ndhunju.ultraprivatemessenger.common

import com.ndhunju.ultraprivatemessenger.common.MutableStateListFlow.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This is a hacky and ugly supplement to [SnapshotStateList](https://developer.android.com/reference/kotlin/androidx/compose/runtime/snapshots/SnapshotStateList)
 * until SnapshotStateList comes part of KMP.
 */
// TODO: Nikesh Add unit test to this
class MutableStateListFlow<T>(
    private val mutableList: MutableList<T> = mutableListOf()
): MutableStateFlow<Operation<T>>, MutableList<T> by mutableList {

    private val operationStateFlow = MutableStateFlow<Operation<T>>(Operation.None())
    //region MutableList APIs

    override val size: Int
        get() = mutableList.size

    override fun addAll(elements: Collection<T>): Boolean {
        val changed = mutableList.addAll(elements)
        if (changed) triggerNotification(Operation.AddAll(elements))
        return changed
    }

    override fun removeAt(index: Int): T {
        val temp = mutableList.removeAt(index)
        triggerNotification(Operation.RemoveAt(index))
        return temp
    }

    override fun add(index: Int, element: T) {
        mutableList.add(index, element)
        triggerNotification(Operation.Add(index, element))
    }

    override operator fun set(index: Int, element: T): T {
        val previous = mutableList[index]
        mutableList[index] = element
        triggerNotification(Operation.Set(index, element))
        return previous
    }

    override fun clear() {
        mutableList.clear()
        triggerNotification(Operation.Clear())
    }

    fun setDataSet(elements: Collection<T>) {
        mutableList.clear()
        mutableList.addAll(elements)
        triggerNotification(Operation.DataSetChanged(elements))
    }

    fun getIndex(element: T): Int? {
        mutableList.forEachIndexed{ i, item ->
            if (item == element) {
                return i
            }
        }

        return null
    }

    private fun triggerNotification(operation: Operation<T>) {
        operationStateFlow.value = operation
    }

    //endregion

    //region MutableStateFlow APIs

    override var value: Operation<T>
        get() { return operationStateFlow.value } // Composer calls this
        //get() { throw UnsupportedOperationException("Don't directly access this property. ") }
        set(_) { throw UnsupportedOperationException("Don't directly set this property. ") }

    override val subscriptionCount: StateFlow<Int>
        get() { return operationStateFlow.subscriptionCount }


    override val replayCache: List<Operation<T>>
        get() { return operationStateFlow.replayCache }

    override suspend fun emit(value: Operation<T>) {
        operationStateFlow.emit(value)
    }

    override fun compareAndSet(expect: Operation<T>, update: Operation<T>): Boolean {
        return operationStateFlow.compareAndSet(expect, update)
    }

    override suspend fun collect(collector: FlowCollector<Operation<T>>): Nothing {
        operationStateFlow.collect(collector)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        operationStateFlow.resetReplayCache()
    }

    override fun tryEmit(value: Operation<T>): Boolean {
        return operationStateFlow.tryEmit(value)
    }

    //endregion

    sealed class Operation<T> {
        class None<T>: Operation<T>()
        class AddAll<T>(val items: Collection<T>): Operation<T>()
        class RemoveAt<T>(val index: Int): Operation<T>()
        class Add<T>(val index: Int, val item: T): Operation<T>()
        class Set<T>(val index: Int, val item: T): Operation<T>()
        class Clear<T> : Operation<T>()
        class DataSetChanged<T>(val items: Collection<T>): Operation<T>()
    }

}