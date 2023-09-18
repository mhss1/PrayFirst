package com.mhss.app.prayfirst.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhss.app.prayfirst.R
import com.mhss.app.prayfirst.data.repository.DataStoreRepository

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        SettingsSectionCard(
            title = stringResource(R.string.lock_screen_on_time_start)
        ) {
            val fajrLockSettings by viewModel.getLockPrayerSettings(DataStoreRepository.fajrLockScreen)
                .collectAsStateWithLifecycle()
            val zuhrLockSettings by viewModel.getLockPrayerSettings(DataStoreRepository.zuhrLockScreen)
                .collectAsStateWithLifecycle()
            val asrLockSettings by viewModel.getLockPrayerSettings(DataStoreRepository.asrLockScreen)
                .collectAsStateWithLifecycle()
            val maghribLockSettings by viewModel.getLockPrayerSettings(DataStoreRepository.maghribLockScreen)
                .collectAsStateWithLifecycle()
            val ishaLockSettings by viewModel.getLockPrayerSettings(DataStoreRepository.ishaLockScreen)
                .collectAsStateWithLifecycle()
            val lockScreenPeriod by viewModel.lockScreenMinutes.collectAsStateWithLifecycle()

            SettingsSwitchItem(
                title = stringResource(R.string.fajr),
                checked = fajrLockSettings,
                onCheckedChange = { checked ->
                    viewModel.togglePrayerLock(DataStoreRepository.fajrLockScreen, checked)
                }
            )
            Divider(Modifier.padding(vertical = 4.dp))
            SettingsSwitchItem(
                title = stringResource(R.string.zuhr),
                checked = zuhrLockSettings,
                onCheckedChange = { checked ->
                    viewModel.togglePrayerLock(DataStoreRepository.zuhrLockScreen, checked)
                }
            )
            Divider(Modifier.padding(vertical = 4.dp))
            SettingsSwitchItem(
                title = stringResource(R.string.asr),
                checked = asrLockSettings,
                onCheckedChange = { checked ->
                    viewModel.togglePrayerLock(DataStoreRepository.asrLockScreen, checked)
                }
            )
            Divider(Modifier.padding(vertical = 4.dp))
            SettingsSwitchItem(
                title = stringResource(R.string.maghrib),
                checked = maghribLockSettings,
                onCheckedChange = { checked ->
                    viewModel.togglePrayerLock(DataStoreRepository.maghribLockScreen, checked)
                }
            )
            Divider(Modifier.padding(vertical = 4.dp))
            SettingsSwitchItem(
                title = stringResource(R.string.isha),
                checked = ishaLockSettings,
                onCheckedChange = { checked ->
                    viewModel.togglePrayerLock(DataStoreRepository.ishaLockScreen, checked)
                }
            )
            Divider(Modifier.padding(vertical = 4.dp))
            SettingsNumberItem(
                title = stringResource(R.string.lock_screen_period),
                stringValue = lockScreenPeriod,
                hint = stringResource(R.string.minutes),
                onValueChange = { viewModel.updateLockMinutes(it) }
            )

        }
    }
}