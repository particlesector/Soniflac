package com.particlesector.soniflac.core.network.interceptor

import com.particlesector.soniflac.core.common.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserAgentInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(UserAgentInterceptor())
            .build()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `interceptor adds User-Agent header`() {
        server.enqueue(MockResponse().setBody("{}"))

        val request = Request.Builder()
            .url(server.url("/test"))
            .build()

        client.newCall(request).execute()

        val recorded = server.takeRequest()
        assertEquals(Constants.RadioBrowser.USER_AGENT, recorded.getHeader("User-Agent"))
    }
}
