package com.particlesector.soniflac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.particlesector.soniflac.ui.SoniFlacApp
import com.particlesector.soniflac.ui.theme.SoniFlacTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoniFlacTheme {
                SoniFlacApp()
            }
        }
    }
}
