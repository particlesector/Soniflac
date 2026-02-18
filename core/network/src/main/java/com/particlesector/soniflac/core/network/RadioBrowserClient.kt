package com.particlesector.soniflac.core.network

import com.particlesector.soniflac.core.common.Constants
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadioBrowserClient @Inject constructor() {

    @Volatile
    private var cachedBaseUrl: String? = null

    suspend fun getBaseUrl(): String {
        cachedBaseUrl?.let { return it }
        return resolveServerUrl().also { cachedBaseUrl = it }
    }

    fun clearCache() {
        cachedBaseUrl = null
    }

    companion object {
        internal fun resolveServerUrl(): String {
            return try {
                val addresses = InetAddress.getAllByName(Constants.RadioBrowser.DNS_LOOKUP_HOST)
                if (addresses.isNotEmpty()) {
                    val host = addresses.random().canonicalHostName
                    "https://$host"
                } else {
                    fallbackUrl()
                }
            } catch (_: Exception) {
                fallbackUrl()
            }
        }

        private fun fallbackUrl(): String = "https://de1.api.radio-browser.info"
    }
}
