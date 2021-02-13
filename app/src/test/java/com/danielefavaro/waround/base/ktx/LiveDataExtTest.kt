package com.danielefavaro.waround.base.ktx

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.danielefavaro.waround.base.util.Event
import junit.framework.TestCase
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 */
fun <T> LiveData<T>.getOrAwaitValues(
    numberOfEvents: Int,
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): List<T?> {
    val data = mutableListOf<T?>()
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data.add(o)
            if (data.size == numberOfEvents) {
                latch.countDown()
                this@getOrAwaitValues.removeObserver(this)
            }
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data
}

/**
 * Observes a [LiveData] until the `block` is done executing.
 */
fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
    val observer = Observer<T> { }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}

suspend fun <T> LiveData<T>.assertValues(
    expectedValues: List<T>,
    trigger: suspend () -> Unit
) {
    val expectedQueueValues = LinkedList(expectedValues)
    val data = mutableListOf<T?>()
    val observer = Observer<T> { value ->
        data.add(value)
    }
    observeForever(observer)
    trigger()
    removeObserver(observer)
    if (expectedQueueValues.count() > 0) {
        TestCase.assertEquals(null, expectedValues, data)
    }
}

suspend fun <T> LiveData<Event<T>>.assertEvents(
    expectedValues: List<T>,
    trigger: suspend () -> Unit
) {
    val expectedQueueValues = LinkedList(expectedValues)
    val data = mutableListOf<T?>()
    val observer = Observer<Event<T>> { event ->
        val value = event.peekContent()
        data.add(value)
    }
    observeForever(observer)
    trigger()
    removeObserver(observer)
    if (expectedQueueValues.count() > 0) {
        TestCase.assertEquals(null, expectedValues, data)
    }
}
