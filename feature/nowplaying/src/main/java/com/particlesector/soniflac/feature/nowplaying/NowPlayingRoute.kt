package com.particlesector.soniflac.feature.nowplaying

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NowPlayingRoute(
    viewModel: NowPlayingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NowPlayingScreen(uiState = uiState)
}

@Composable
internal fun NowPlayingScreen(
    uiState: NowPlayingUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = uiState.title.ifEmpty { "Nothing Playing" },
            style = MaterialTheme.typography.headlineMedium,
        )
        if (uiState.artist.isNotEmpty()) {
            Text(
                text = uiState.artist,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
