package com.mhss.app.prayfirst.presentation.settings

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhss.app.prayfirst.data.repository.DataStoreRepository
import com.mhss.app.prayfirst.domain.repository.PrayerAlarmRepository
import com.mhss.app.prayfirst.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PreferencesRepository,
    private val alarms: PrayerAlarmRepository
) : ViewModel() {

    fun getLockPrayerSettings(key: Preferences.Key<Boolean>) = prefs
        .get(key, true)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), true)

    private val _lockScreenMinutesString: MutableStateFlow<String> = MutableStateFlow("10")
    val lockScreenMinutes: StateFlow<String> = _lockScreenMinutesString

    init {
        viewModelScope.launch {
            _lockScreenMinutesString.value =
                prefs.get(DataStoreRepository.overlayMinutes, 10).first().toString()
            _lockScreenMinutesString
                .debounce(1000)
                .map { it.toIntOrNull() }
                .filterNotNull()
                .collectLatest {
                    prefs.save(DataStoreRepository.overlayMinutes, it)
                }
        }
    }

    fun updateLockMinutes(stringValue: String) {
        _lockScreenMinutesString.update { stringValue }
    }


    fun togglePrayerLock(key: Preferences.Key<Boolean>, checked: Boolean) = viewModelScope.launch {
        launch {
            prefs.save(key, checked)
        }
        launch {
            if (checked) alarms.scheduleNewAlarm(DataStoreRepository.lockKeyToType(key))
            else alarms.cancelAlarm(DataStoreRepository.lockKeyToType(key))
        }
    }

}