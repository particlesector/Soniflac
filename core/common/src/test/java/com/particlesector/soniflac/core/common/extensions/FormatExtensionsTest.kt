package com.particlesector.soniflac.core.common.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormatExtensionsTest {

    @Test
    fun `formatBytes returns B for small values`() {
        assertEquals("0 B", 0L.formatBytes())
        assertEquals("512 B", 512L.formatBytes())
        assertEquals("1023 B", 1023L.formatBytes())
    }

    @Test
    fun `formatBytes returns KB for kilobyte range`() {
        assertEquals("1.0 KB", 1024L.formatBytes())
        assertEquals("1.5 KB", 1536L.formatBytes())
    }

    @Test
    fun `formatBytes returns MB for megabyte range`() {
        assertEquals("1.0 MB", (1024L * 1024).formatBytes())
        assertEquals("34.2 MB", (35_864_371L).formatBytes())
    }

    @Test
    fun `formatBytes returns GB for gigabyte range`() {
        assertEquals("1.00 GB", (1024L * 1024 * 1024).formatBytes())
        assertEquals("4.20 GB", (4_509_715_660L).formatBytes())
    }

    @Test
    fun `formatSampleRate formats with commas`() {
        assertEquals("44,100 Hz", 44100.formatSampleRate())
        assertEquals("96,000 Hz", 96000.formatSampleRate())
        assertEquals("192,000 Hz", 192000.formatSampleRate())
    }

    @Test
    fun `formatSampleRate handles small values`() {
        assertEquals("500 Hz", 500.formatSampleRate())
    }

    @Test
    fun `formatBitrate converts to kbps`() {
        assertEquals("128 kbps", 128000.formatBitrate())
        assertEquals("320 kbps", 320000.formatBitrate())
        assertEquals("923 kbps", 923000.formatBitrate())
    }

    @Test
    fun `formatBitrate returns bps for small values`() {
        assertEquals("500 bps", 500.formatBitrate())
    }

    @Test
    fun `formatDuration formats minutes and seconds`() {
        assertEquals("0:00", 0L.formatDuration())
        assertEquals("1:00", 60_000L.formatDuration())
        assertEquals("3:45", 225_000L.formatDuration())
    }

    @Test
    fun `formatDuration formats hours`() {
        assertEquals("1:00:00", 3_600_000L.formatDuration())
        assertEquals("2:30:15", 9_015_000L.formatDuration())
    }

    @Test
    fun `formatChannels returns descriptive names`() {
        assertEquals("Mono", 1.formatChannels())
        assertEquals("Stereo", 2.formatChannels())
        assertEquals("6 ch", 6.formatChannels())
    }
}
