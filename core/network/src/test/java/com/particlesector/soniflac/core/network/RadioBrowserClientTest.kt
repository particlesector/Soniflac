package com.particlesector.soniflac.core.network

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RadioBrowserClientTest {

    private lateinit var client: RadioBrowserClient

    @BeforeEach
    fun setUp() {
        client = RadioBrowserClient()
    }

    @Test
    fun `getBaseUrl returns https url`() = runTest {
        val url = client.getBaseUrl()
        assertTrue(url.startsWith("https://"))
    }

    @Test
    fun `getBaseUrl caches result`() = runTest {
        val first = client.getBaseUrl()
        val second = client.getBaseUrl()
        assertEquals(first, second)
    }

    @Test
    fun `clearCache resets cached url`() = runTest {
        client.getBaseUrl()
        client.clearCache()
        val url = client.getBaseUrl()
        assertNotNull(url)
    }

    @Test
    fun `getBaseUrlBlocking returns valid url`() {
        val url = client.getBaseUrlBlocking()
        assertTrue(url.startsWith("https://"))
    }

    @Test
    fun `resolveServerUrl returns fallback on failure`() = runTest {
        val url = RadioBrowserClient.resolveServerUrl()
        assertTrue(url.startsWith("https://"))
    }

    @Test
    fun `fallback url is valid`() = runTest {
        val url = RadioBrowserClient.resolveServerUrl()
        assertNotNull(url)
        assertTrue(url.contains("radio-browser") || url.contains("api"))
    }
}
