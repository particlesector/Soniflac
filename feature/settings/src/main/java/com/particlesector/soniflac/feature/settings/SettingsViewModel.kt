package com.particlesector.soniflac.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.particlesector.soniflac.billing.BillingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isPremium: Boolean = false,
    val monthlyDataLimitBytes: Long? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val billingManager: BillingManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            billingManager.isPremium.collect { premium ->
                _uiState.value = _uiState.value.copy(isPremium = premium)
            }
        }
    }
}
