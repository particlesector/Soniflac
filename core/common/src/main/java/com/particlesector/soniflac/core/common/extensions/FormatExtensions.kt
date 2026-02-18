package com.particlesector.soniflac.core.common.extensions

import java.util.Locale

fun Long.formatBytes(): String {
    if (this < 1024) return "$this B"
    val kb = this / 1024.0
    if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format(Locale.US, "%.2f GB", gb)
}

fun Int.formatSampleRate(): String = when {
    this >= 1000 -> String.format(Locale.US, "%,d Hz", this)
    else -> "$this Hz"
}

fun Int.formatBitrate(): String = when {
    this >= 1000 -> "${this / 1000} kbps"
    else -> "$this bps"
}

fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}

fun Int.formatChannels(): String = when (this) {
    1 -> "Mono"
    2 -> "Stereo"
    else -> "$this ch"
}
