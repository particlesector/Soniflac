package com.particlesector.soniflac.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.particlesector.soniflac.billing.BillingManager
import com.particlesector.soniflac.core.database.repository.DataUsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val billingManager: BillingManager,
    private val dataUsageRepository: DataUsageRepository,
) : ViewModel() {

    private val _monthlyDataLimitMb = MutableStateFlow(0L)

    val uiState: StateFlow<SettingsUiState> = combine(
        billingManager.isPremium,
        _monthlyDataLimitMb,
        dataUsageRepository.observeMonthly(),
    ) { isPremium, limitMb, monthlyBytes ->
        SettingsUiState(
            isPremium = isPremium,
            monthlyDataLimitMb = limitMb,
            monthlyDataUsedBytes = monthlyBytes,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    init {
        viewModelScope.launch {
            billingManager.queryPremiumStatus()
        }
    }

    fun setMonthlyDataLimit(limitMb: Long) {
        _monthlyDataLimitMb.value = limitMb
    }
}

data class SettingsUiState(
    val isPremium: Boolean = false,
    val monthlyDataLimitMb: Long = 0,
    val monthlyDataUsedBytes: Long = 0,
)
