package com.mhss.app.prayfirst.presentation.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mhss.app.prayfirst.R

@SuppressLint("BatteryLife")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onShowSnackBar: (String) -> Unit
) {
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    val locationText by viewModel.location.collectAsStateWithLifecycle()
    val latestPrayerTimes by viewModel.latestPrayerTimes.collectAsStateWithLifecycle()
    val nextPrayerData by viewModel.nextPrayerData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var canDrawOverlays by remember {
        mutableStateOf(
            Settings.canDrawOverlays(context)
        )
    }
    val powerManager = remember {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    val ignoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(context.packageName)

    var editMode by remember { mutableStateOf(false) }
    val drawOverlaysPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        canDrawOverlays = Settings.canDrawOverlays(context)
    }
    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) {
            onShowSnackBar(
                context.getString(R.string.gps_fail_message)
            )
        }
    }
    val locationPermission = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(loadingState) {
        editMode = loadingState is LoadingState.Loading || loadingState is LoadingState.Error
        if (loadingState is LoadingState.Error) {
            when (loadingState) {
                is LoadingState.Error.Internet -> onShowSnackBar(
                    context.getString(
                        R.string.internet_error
                    )
                )

                is LoadingState.Error.NoDataForLocation -> onShowSnackBar(
                    context.getString(
                        R.string.no_data_for_location
                    )
                )

                is LoadingState.Error.LocationDetectionFailed -> onShowSnackBar(
                    context.getString(
                        R.string.location_detection_failed
                    )
                )

                else -> onShowSnackBar(
                    context.getString(
                        R.string.unknown_error
                    )
                )
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp, horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (canDrawOverlays) {
            LocationCard(
                currentLocation = locationText,
                editMode = editMode,
                loading = loadingState is LoadingState.Loading,
                onDetectLocation = {
                    if (locationPermission.permissions.any { it.status.isGranted }) {
                        if (context.isGPSEnabled()) {
                            viewModel.getPrayerTimesForDetectedLocation()
                        } else {
                            context.requestGPS(
                                onSuccess = {
                                    viewModel.getPrayerTimesForDetectedLocation()
                                },
                                onFailure = { exception ->
                                    gpsResolutionLauncher.launch(
                                        IntentSenderRequest.Builder(exception.resolution).build()
                                    )
                                }
                            )
                        }
                    } else if (locationPermission.shouldShowRationale) {
                        onShowSnackBar(
                            context.getString(
                                R.string.location_permission_rationale
                            )
                        )
                    } else {
                        locationPermission.launchMultiplePermissionRequest()
                    }
                },
                onClick = {
                    editMode = !editMode
                },
                onSearch = {
                    viewModel.getPrayerTimesForAddress(it)
                }
            )
            AnimatedVisibility(!ignoringBatteryOptimizations) {
                BatteryOptimizationAlertCard {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${context.applicationContext.packageName}")
                    )
                    context.startActivity(intent)
                }
            }
            nextPrayerData?.let { prayerData ->
                Spacer(Modifier.height(12.dp))
                PrayerCountdownIndicator(
                    Modifier
                        .fillMaxWidth(0.75f),
                    prayerData
                )
            }
            Spacer(Modifier.height(18.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .alpha(if (loadingState is LoadingState.Loading) 0.5f else 1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                latestPrayerTimes.forEach { prayerTime ->
                    PrayerCard(
                        prayer = prayerTime,
                        isNextPrayer = nextPrayerData?.prayer?.nameResId == prayerTime.nameResId
                    )
                }
            }
        } else {
            NoOverLayPermissionCard {
                drawOverlaysPermissionLauncher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.applicationContext.packageName)
                    )
                )
            }
        }
    }
}

@Composable
fun NoOverLayPermissionCard(
    onGrantPermission: () -> Unit
) {
    Text(
        text = stringResource(R.string.no_overlay_permission_message),
        modifier = Modifier.padding(12.dp),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(12.dp))
    TextButton(
        onClick = { onGrantPermission() },
    ) {
        Text(
            text = stringResource(R.string.grant_permission),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}