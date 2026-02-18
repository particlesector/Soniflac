package com.particlesector.soniflac.feature.streamstats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StreamStatsOverlay(
    viewModel: StreamStatsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StreamStatsContent(uiState = uiState)
}

@Composable
internal fun StreamStatsContent(
    uiState: StreamStatsUiState,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Stream Stats", style = MaterialTheme.typography.titleMedium)
            if (uiState.codec.isNotEmpty()) {
                Text("Codec: ${uiState.codec}")
            }
            if (uiState.sampleRate > 0) {
                Text("Sample Rate: ${uiState.sampleRate} Hz")
            }
            if (uiState.bitrate > 0) {
                Text("Bitrate: ${uiState.bitrate / 1000} kbps")
            }
        }
    }
}
