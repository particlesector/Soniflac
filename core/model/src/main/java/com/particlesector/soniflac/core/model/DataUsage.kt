package com.particlesector.soniflac.core.model

import java.time.LocalDate

data class DataUsage(
    val date: LocalDate,
    val bytesStreamed: Long,
    val streamingDurationMs: Long,
)
