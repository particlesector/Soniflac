package com.particlesector.soniflac.core.common.extensions

import com.particlesector.soniflac.core.common.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FlowExtensionsTest {

    @Test
    fun `asResult emits Loading then Success`() = runTest {
        val results = flow { emit("data") }
            .asResult()
            .toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals("data", (results[1] as Result.Success).data)
    }

    @Test
    fun `asResult emits Loading then Error on exception`() = runTest {
        val results = flow<String> { throw RuntimeException("boom") }
            .asResult()
            .toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        assertEquals("boom", (results[1] as Result.Error).exception.message)
    }

    @Test
    fun `asResult emits Loading then multiple Success values`() = runTest {
        val results = flow {
            emit(1)
            emit(2)
            emit(3)
        }.asResult().toList()

        assertEquals(4, results.size)
        assertTrue(results[0] is Result.Loading)
        assertEquals(1, (results[1] as Result.Success).data)
        assertEquals(2, (results[2] as Result.Success).data)
        assertEquals(3, (results[3] as Result.Success).data)
    }
}
