package com.particlesector.soniflac.core.player

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BandwidthTrackerTest {

    private lateinit var tracker: BandwidthTracker

    @BeforeEach
    fun setUp() {
        tracker = BandwidthTracker()
    }

    @Test
    fun `initial state is zero bytes`() {
        assertEquals(0L, tracker.totalBytes.value)
        assertEquals(0L, tracker.sessionBytes.value)
    }

    @Test
    fun `recordBytes increments total bytes`() {
        tracker.recordBytes(100)
        tracker.recordBytes(200)
        assertEquals(300L, tracker.totalBytes.value)
    }

    @Test
    fun `recordBytes increments session bytes`() {
        tracker.recordBytes(500)
        assertEquals(500L, tracker.sessionBytes.value)
    }

    @Test
    fun `resetSession clears session bytes`() {
        tracker.recordBytes(1000)
        tracker.resetSession()
        assertEquals(0L, tracker.sessionBytes.value)
    }

    @Test
    fun `resetSession preserves total bytes`() {
        tracker.recordBytes(1000)
        tracker.resetSession()
        assertEquals(1000L, tracker.totalBytes.value)
    }

    @Test
    fun `startSession resets session bytes`() = runTest {
        tracker.recordBytes(500)
        tracker.startSession { _, _ -> }
        assertEquals(0L, tracker.sessionBytes.value)
    }

    @Test
    fun `stopSession stops flush loop`() = runTest {
        tracker.startSession { _, _ -> }
        tracker.recordBytes(100)
        tracker.stopSession()
        assertEquals(100L, tracker.totalBytes.value)
    }

    @Test
    fun `multiple sessions accumulate total bytes`() = runTest {
        tracker.startSession { _, _ -> }
        tracker.recordBytes(100)
        tracker.stopSession()

        tracker.startSession { _, _ -> }
        tracker.recordBytes(200)
        tracker.stopSession()

        assertEquals(300L, tracker.totalBytes.value)
    }

    @Test
    fun `session bytes isolated between sessions`() = runTest {
        tracker.startSession { _, _ -> }
        tracker.recordBytes(100)
        tracker.stopSession()

        tracker.startSession { _, _ -> }
        assertEquals(0L, tracker.sessionBytes.value)
        tracker.recordBytes(200)
        assertEquals(200L, tracker.sessionBytes.value)
        tracker.stopSession()
    }
}
