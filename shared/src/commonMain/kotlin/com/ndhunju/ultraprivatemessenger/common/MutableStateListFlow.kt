package com.ndhunju.ultraprivatemessenger.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This is a hacky and ugly supplement to [SnapshotStateList](https://developer.android.com/reference/kotlin/androidx/compose/runtime/snapshots/SnapshotStateList)
 * until SnapshotStateList comes part of KMP.
 */
class MutableStateListFlow<T>(
    private val mutableList: MutableList<T> = mutableListOf()
): Iterable<T>, MutableStateFlow<List<T>>, MutableList<T> by mutableList {

    private val mutableStateFlow = MutableStateFlow<List<T>>(emptyList())

    //region MutableList APIs

    override val size: Int
        get() = mutableList.size

    override fun addAll(elements: Collection<T>): Boolean {
        val changed = mutableList.addAll(elements)
        if (changed) triggerNotification()
        return changed
    }

    override fun removeAt(index: Int): T {
        val temp = mutableList.removeAt(index)
        triggerNotification()
        return temp
    }

    override fun add(index: Int, element: T) {
        mutableList.add(index, element)
        triggerNotification()
    }

    override operator fun set(index: Int, element: T): T {
        val previous = mutableList[index]
        mutableList[index] = element
        triggerNotification()
        return previous
    }

    override fun clear() {
        mutableList.clear()
        triggerNotification()
    }

    fun replaceAll(elements: Collection<T>): Boolean {
        mutableList.clear()
        return addAll(elements)
    }

    fun getIndex(element: T): Int? {
        mutableList.forEachIndexed{ i, item ->
            if (item == element) {
                return i
            }
        }

        return null
    }

    private fun triggerNotification() {
        // In order for collectors to be notified, we have to create a new mutable list
        // This is a very ugly way but will do for now. In future, hopefully we can use
        // mutableStateListOf() or SnapshotStateList when it becomes part of KMP
        mutableStateFlow.value = mutableListOf<T>().apply {
            addAll(mutableList)
        }
    }

    //endregion

    //region MutableStateFlow APIs

    override var value: List<T>
        get() { return mutableStateFlow.value } // Composer calls this
        //get() { throw UnsupportedOperationException("Don't directly access this property. ") }
        set(_) { throw UnsupportedOperationException("Don't directly set this property. ") }

    override val subscriptionCount: StateFlow<Int>
        get() { return mutableStateFlow.subscriptionCount }


    override val replayCache: List<List<T>>
        get() { return mutableStateFlow.replayCache }

    override suspend fun emit(value: List<T>) {
        mutableStateFlow.emit(value)
    }

    override fun compareAndSet(expect: List<T>, update: List<T>): Boolean {
        return mutableStateFlow.compareAndSet(expect, update)
    }

    override suspend fun collect(collector: FlowCollector<List<T>>): Nothing {
        mutableStateFlow.collect(collector)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        mutableStateFlow.resetReplayCache()
    }

    override fun tryEmit(value: List<T>): Boolean {
        return mutableStateFlow.tryEmit(value)
    }

    //endregion

}