package com.particlesector.soniflac.core.common

object Constants {
    const val FREE_FAVORITE_LIMIT = 5
    const val DATA_USAGE_FLUSH_INTERVAL_MS = 30_000L
    const val STREAM_METRICS_POLL_INTERVAL_MS = 1_000L
    const val DATA_WARNING_THRESHOLD = 0.80f
    const val DATA_ALERT_THRESHOLD = 1.00f

    object RadioBrowser {
        const val DNS_LOOKUP_HOST = "all.api.radio-browser.info"
        const val USER_AGENT = "SoniFlac/1.0"
    }
}
