package com.particlesector.soniflac.core.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioFocusManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var onFocusChange: ((FocusState) -> Unit)? = null

    fun requestFocus(onFocusChange: (FocusState) -> Unit): Boolean {
        this.onFocusChange = onFocusChange
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(attributes)
            .setOnAudioFocusChangeListener { focusChange ->
                val state = when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> FocusState.GAINED
                    AudioManager.AUDIOFOCUS_LOSS -> FocusState.LOST
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> FocusState.LOST_TRANSIENT
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> FocusState.DUCK
                    else -> FocusState.LOST
                }
                onFocusChange(state)
            }
            .build()

        focusRequest = request
        return audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonFocus() {
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        focusRequest = null
        onFocusChange = null
    }

    enum class FocusState {
        GAINED, LOST, LOST_TRANSIENT, DUCK
    }
}
