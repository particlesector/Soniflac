package com.particlesector.soniflac.core.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ResultTest {

    @Test
    fun `asSuccess returns data for Success`() {
        val result: Result<String> = Result.Success("hello")
        assertEquals("hello", result.asSuccess())
    }

    @Test
    fun `asSuccess returns null for Error`() {
        val result: Result<String> = Result.Error(RuntimeException("fail"))
        assertNull(result.asSuccess())
    }

    @Test
    fun `asSuccess returns null for Loading`() {
        val result: Result<String> = Result.Loading
        assertNull(result.asSuccess())
    }

    @Test
    fun `asError returns exception for Error`() {
        val exception = RuntimeException("fail")
        val result: Result<String> = Result.Error(exception)
        assertEquals(exception, result.asError())
    }

    @Test
    fun `asError returns null for Success`() {
        val result: Result<String> = Result.Success("hello")
        assertNull(result.asError())
    }

    @Test
    fun `isLoading returns true for Loading`() {
        val result: Result<String> = Result.Loading
        assertTrue(result.isLoading())
    }

    @Test
    fun `map transforms Success data`() {
        val result: Result<Int> = Result.Success(5)
        val mapped = result.map { it * 2 }
        assertEquals(10, mapped.asSuccess())
    }

    @Test
    fun `map preserves Error`() {
        val exception = RuntimeException("fail")
        val result: Result<Int> = Result.Error(exception)
        val mapped = result.map { it * 2 }
        assertEquals(exception, mapped.asError())
    }

    @Test
    fun `map preserves Loading`() {
        val result: Result<Int> = Result.Loading
        val mapped = result.map { it * 2 }
        assertTrue(mapped.isLoading())
    }

    @Test
    fun `onSuccess invokes action for Success`() {
        var captured: String? = null
        val result: Result<String> = Result.Success("hello")
        result.onSuccess { captured = it }
        assertEquals("hello", captured)
    }

    @Test
    fun `onSuccess does not invoke action for Error`() {
        var invoked = false
        val result: Result<String> = Result.Error(RuntimeException())
        result.onSuccess { invoked = true }
        assertTrue(!invoked)
    }

    @Test
    fun `onError invokes action for Error`() {
        val exception = RuntimeException("fail")
        var captured: Throwable? = null
        val result: Result<String> = Result.Error(exception)
        result.onError { captured = it }
        assertEquals(exception, captured)
    }

    @Test
    fun `onError does not invoke action for Success`() {
        var invoked = false
        val result: Result<String> = Result.Success("hello")
        result.onError { invoked = true }
        assertTrue(!invoked)
    }
}
