package com.mhss.app.prayfirst.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mhss.app.prayfirst.R
import com.mhss.app.prayfirst.data.repository.DataStoreRepository
import com.mhss.app.prayfirst.domain.model.PrayerTimeType
import com.mhss.app.prayfirst.domain.repository.PrayerAlarmRepository
import com.mhss.app.prayfirst.domain.repository.PreferencesRepository
import com.mhss.app.prayfirst.presentation.overlay.OverlayLifecycleOwner
import com.mhss.app.prayfirst.presentation.overlay.PrayFirstOverlay
import com.mhss.app.prayfirst.util.formatTimerTime
import com.mhss.app.prayfirst.util.isFriday
import com.mhss.app.prayfirst.util.now
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PrayFirstOverlayService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var audioManager: AudioManager
    private var overlay: View? = null
    private var timerText by mutableStateOf("")
    private var progress by mutableFloatStateOf(1f)

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Inject
    lateinit var alarmRepository: PrayerAlarmRepository
    @Inject
    lateinit var prefs: PreferencesRepository

    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val prayerType = intent?.getIntExtra("type", -1)
        if (prayerType == null || prayerType == -1) {
            stopService()
            return START_NOT_STICKY
        }
        serviceScope.launch {
            alarmRepository.scheduleNextAlarm(prayerType)
        }

        requestAudioFocus()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        overlay = ComposeView(this).apply {
            setContent {
                PrayFirstOverlay(
                    prayerType.prayerNameResFromType(),
                    timerText,
                    progress
                )
            }
        }
        overlay?.handleOverlayComposableLifecycle()

        wm.addView(overlay, params)

        startCountdown()

        return START_REDELIVER_INTENT
    }

    private fun startCountdown() = serviceScope.launch {

        val lockTimeMillis = prefs.get(DataStoreRepository.overlayMinutes, 10).first() * 60 * 1000

        val startTimeMillis = now()
        val endTimeMillis = startTimeMillis + lockTimeMillis

        while (now() < endTimeMillis) {
            val remainingMillis = endTimeMillis - now()

            timerText = remainingMillis.formatTimerTime()
            progress =  1 - remainingMillis.toFloat() / lockTimeMillis

            delay(1000)
        }

        stopService()
    }

    private fun View.handleOverlayComposableLifecycle() {
        val lifecycleOwner = OverlayLifecycleOwner()
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recomposer = Recomposer(coroutineContext)
        compositionContext = recomposer
        runRecomposeScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
    }

    private fun requestAudioFocus() {
        audioManager.requestAudioFocus(
            AudioFocusRequest
                .Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .build()
        )
    }

    private fun Int.prayerNameResFromType() = when (this) {
        PrayerTimeType.FAJR.ordinal -> R.string.fajr
        PrayerTimeType.SUNRISE.ordinal -> R.string.sunrise
        PrayerTimeType.ZUHR.ordinal -> if (isFriday()) R.string.jumuaa else R.string.zuhr
        PrayerTimeType.ASR.ordinal -> R.string.asr
        PrayerTimeType.MAGHRIB.ordinal -> R.string.maghrib
        PrayerTimeType.ISHA.ordinal -> R.string.isha
        else -> R.string.fajr
    }

    private fun stopService() {
        serviceScope.cancel()
        wm.removeView(overlay)
        overlay = null
        stopSelf()
    }

    override fun onBind(p0: Intent?) = null
}