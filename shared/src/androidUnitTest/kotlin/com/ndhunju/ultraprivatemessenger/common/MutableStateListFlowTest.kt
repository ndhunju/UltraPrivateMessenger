package com.ndhunju.ultraprivatemessenger.common

import com.ndhunju.ultraprivatemessenger.common.MutableStateListFlow.Operation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration

class MutableStateListFlowTest {

    private lateinit var mutableStateListFlow: MutableStateListFlow<Int>

    @Before
    fun setUp() {
        mutableStateListFlow = MutableStateListFlow()
    }

    @Test
    fun `whenNoOperationIsPerformed thenNoneOperationShouldBeEmitted`()  {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            // Then
            val operation = mutableStateListFlow.first()
            Assert.assertEquals(Operation.None::class, operation::class)
        }
    }

    @Test
    fun `whenOperationsArePerformed thenNoneOperationShouldNotBeEmitted`()  {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            // When
            mutableStateListFlow.add(0, 1)
            mutableStateListFlow.removeAt(0)

            // Then
            val operation = mutableStateListFlow.first()
            Assert.assertNotEquals(Operation.None::class, operation::class)
        }
    }

    @Test
    fun `whenAnItemIsAdded thenAddOperationShouldBeEmitted`()  {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            // When
            mutableStateListFlow.add(0, 1)

            // Then
            val operation =  mutableStateListFlow.first()
            Assert.assertEquals(Operation.Add::class, operation::class)
            val addOperation = operation as Operation.Add
            Assert.assertEquals(1, addOperation.item)
            Assert.assertEquals(0, addOperation.index)
        }
    }

    @Test
    fun `whenItemsAreAdded thenAddAllOperationShouldBeEmitted`()  {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            // When
            mutableStateListFlow.addAll(listOf(1,2,3))

            // Then
            val operation =  mutableStateListFlow.first()
            Assert.assertEquals(Operation.AddAll::class, operation::class)
            val addOperation = operation as Operation.AddAll
            Assert.assertEquals(1, addOperation.items.first())
            Assert.assertEquals(3, addOperation.items.size)
        }
    }

    @Test
    fun `whenRemoveAllIsCalled thenRemoveAllOperationShouldBeEmitted`()  {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            val removeAtIndex = 0
            mutableStateListFlow.addAll(listOf(1,2,3))
            // When
            mutableStateListFlow.removeAt(removeAtIndex)

            // Then
            val operation =  mutableStateListFlow.first()
            Assert.assertEquals(Operation.RemoveAt::class, operation::class)
            val removeAt = operation as Operation.RemoveAt
            Assert.assertEquals("Remove at index not match", removeAtIndex, removeAt.index)
            Assert.assertEquals("Size not match", 2, mutableStateListFlow.size)
        }
    }

    @Test
    fun `whenAnItemIsSet thenSetOperationShouldBeEmitted`() {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            val setAtIndex = 3
            val setItem = 11
            mutableStateListFlow.addAll(listOf(0,0,0,0))
            // When
            mutableStateListFlow[setAtIndex] = setItem

            // Then
            val operation = mutableStateListFlow.first()
            Assert.assertEquals(Operation.Set::class, operation::class)
            val operationSet = operation as Operation.Set
            Assert.assertEquals(setAtIndex, operationSet.index)
            Assert.assertEquals(setItem, operationSet.item)
        }
    }

    @Test
    fun `whenClearIsCalled thenClearOperationShouldBeEmitted`() {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            // When
            mutableStateListFlow.clear()

            // Then
            val operation = mutableStateListFlow.first()
            Assert.assertEquals(Operation.Clear::class, operation::class)
        }
    }

    @Test
    fun `whenDataSetIsChanged thenDataSetChangeOperationShouldBeEmitted`() {
        runTest(timeout = Duration.parse(DEFAULT_TIME_OUT)) {
            val newItems = listOf(9,10,11)
            // When
            mutableStateListFlow.setDataSet(newItems)

            // Then
            val operation = mutableStateListFlow.first()
            Assert.assertEquals(Operation.DataSetChanged::class, operation::class)
            val dataSetChangedOperation = operation as Operation.DataSetChanged
            Assert.assertArrayEquals(newItems.toIntArray(), dataSetChangedOperation.items.toIntArray())
        }
    }

}